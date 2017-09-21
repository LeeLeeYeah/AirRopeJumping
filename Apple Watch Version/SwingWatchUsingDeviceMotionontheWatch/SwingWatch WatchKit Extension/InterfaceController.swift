/*
 Copyright (C) 2016 Apple Inc. All Rights Reserved.
 See LICENSE.txt for this sample’s licensing information
 
 Abstract:
 This class is responsible for managing interactions with the interface.
 */

import WatchKit
import Foundation
import Dispatch


class InterfaceController: WKInterfaceController, WorkoutManagerDelegate {
    // MARK: Properties
    let workoutManager = WorkoutManager()
    var forehandCount = 0
    var started=false

    // MARK: Interface Properties
    
    @IBOutlet weak var titleLabel: WKInterfaceLabel!
    @IBOutlet weak var forehandCountLabel: WKInterfaceLabel!
    @IBOutlet weak var startButton: WKInterfaceButton!

    // MARK: Initialization
    
    override init() {
        super.init()
        workoutManager.delegate = self
    }

    // MARK: WKInterfaceController
    
    override func willActivate() {
        super.willActivate()
        // On re-activation, update with the cached values.
        updateLabels()
    }

    override func didDeactivate() {
        super.didDeactivate()
    }

    // MARK: Interface Bindings
    
    @IBAction func start() {
//        titleLabel.setText("Workout started")
        if(started==false){
            workoutManager.startWorkout()
            startButton.setBackgroundColor(UIColor.init(red: 178, green: 34, blue: 34, alpha: 1));
            startButton.setTitle("Stop")
            started=true
        }else{
            workoutManager.stopWorkout()
            startButton.setBackgroundColor(UIColor.init(red: 61, green: 145, blue: 64, alpha: 1));
            startButton.setTitle("Start")
            started=false
        }
    }


    // MARK: WorkoutManagerDelegate
    func didUpdateForehandSwingCount(_ manager: WorkoutManager, forehandCount: Int) {
        /// Serialize the property access and UI updates on the main queue.
        DispatchQueue.main.async {
            self.forehandCount = forehandCount
            self.updateLabels()
        }
        
    }
    // MARK: Convenience
    func updateLabels() {
        forehandCountLabel.setText("\(forehandCount)")
    }

}
