/*
 Copyright (C) 2016 Apple Inc. All Rights Reserved.
 See LICENSE.txt for this sample’s licensing information
 
 Abstract:
 This class manages the CoreMotion interactions and 
         provides a delegate to indicate changes in data.
 */

import Foundation
import CoreMotion
import WatchKit
import AVFoundation
import Alamofire

/**
 `MotionManagerDelegate` exists to inform delegates of motion changes.
 These contexts can be used to enable application specific behavior.
 */
protocol MotionManagerDelegate: class {
    func didUpdateForehandSwingCount(_ manager: MotionManager, forehandCount: Int)
}

class MotionManager {
    // MARK: Properties
    
    var _audioPlayer : AVAudioPlayerNode!
    var _audioEngine : AVAudioEngine!
    var str:String = ""
    
    func playAudio()
    {
        if (_audioPlayer==nil) {
            _audioPlayer = AVAudioPlayerNode()
            _audioEngine = AVAudioEngine()
            _audioEngine.attach(_audioPlayer)
            
            let stereoFormat = AVAudioFormat(standardFormatWithSampleRate: 44100, channels: 2)
            _audioEngine.connect(_audioPlayer, to: _audioEngine.mainMixerNode, format: stereoFormat)
            do {
                
                if !_audioEngine.isRunning {
                    try _audioEngine.start()
                }
            } catch {}
        }
        if let path = Bundle.main.path(forResource: "bian3", ofType: "mp3") {
            let fileUrl = URL(fileURLWithPath: path)
            do {
                let asset = try AVAudioFile(forReading: fileUrl)
                _audioPlayer.scheduleFile(asset, at: nil, completionHandler: nil)
                _audioPlayer.play()
                
            } catch {
                print ("asset error")
            }
        }
    }
    
    let motionManager = CMMotionManager()
    let queue = OperationQueue()
    // MARK: Application Specific Constants
    
    // These constants were derived from data and should be further tuned for your needs.
    var now=0.0
    var upFlag=false
    var downFlag=false
    var lastUp = 0.0
    var lastDown = 0.0
    
    
    // The app is using 50hz data and the buffer is going to hold 1s worth of data.
    let sampleInterval = 1.0 / 25
    let rateAlongGravityBuffer = RunningBuffer(size: 25)
    weak var delegate: MotionManagerDelegate?
    /// Swing counts.
    var forehandCount = 0

    // MARK: Initialization
    
    init() {
        // Serial queue for sample handling and calculations.
        queue.maxConcurrentOperationCount = 1
        queue.name = "MotionManagerQueue"
    }

    // MARK: Motion Manager

    func startUpdates() {
        if !motionManager.isDeviceMotionAvailable {
            print("Device Motion is not available.")
            return
        }
        resetAllState()// Reset everything when we start.

        motionManager.deviceMotionUpdateInterval = sampleInterval
        motionManager.startDeviceMotionUpdates(to: queue) { (deviceMotion: CMDeviceMotion?, error: Error?) in
            if error != nil {
                print("Encountered error: \(error!)")
            }
            if deviceMotion != nil {
                self.processDeviceMotion(deviceMotion!)
            }
        }
        str=""
        now=0.0
        upFlag=false
        downFlag=false
        lastUp = 0.0
        lastDown = 0.0
        
    }

    func stopUpdates() {
        if motionManager.isDeviceMotionAvailable {
            motionManager.stopDeviceMotionUpdates()
        }
        print(str)
    }

    // MARK: Motion Processing
    
    func processDeviceMotion(_ deviceMotion: CMDeviceMotion) {
        
        now=now+sampleInterval*2
        
        let gravity = deviceMotion.gravity
        let rotationRate = deviceMotion.rotationRate
        
        let rateAlongGravity = rotationRate.x * gravity.x // r⃗ · ĝ
            + rotationRate.y * gravity.y
            + rotationRate.z * gravity.z
        rateAlongGravityBuffer.addSample(rateAlongGravity)
        
        if ((upFlag==false || now-lastUp>0.7) && rateAlongGravity>1.5){
            upFlag=true
            lastUp=now
            return
        }
        
        if (upFlag==true && rateAlongGravity < (-1.5) && now-lastDown > 0.15 && now-lastUp < 1.0 ){
            upFlag=false
            lastDown=now
            //jump
            incrementForehandCountAndUpdateDelegate()
            str=str+String(now)+","
//            playAudio()
        }
        
    }

    // MARK: Data and Delegate Management
    
    func resetAllState() {
        rateAlongGravityBuffer.reset()
        forehandCount = 0
        updateForehandSwingDelegate()
    }

    func incrementForehandCountAndUpdateDelegate() {
        forehandCount += 1
        //debugPrint("Jump Count: \(forehandCount)")
        
        updateForehandSwingDelegate()
    }

    func updateForehandSwingDelegate() {
        delegate?.didUpdateForehandSwingCount(self, forehandCount:forehandCount)
    }

}
