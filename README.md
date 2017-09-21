# Air Rope Jumping
This is a interesting rope jumping Android app. It has gained 10,000+ downloads on 360 APP Market. It’s also available on Google Play. I also developed an Apple Watch version.

# ANDROID VERSION
It’s a virtual rope jumping app. Users can a cell phone instead of a real rope to do rope jumping. The app will count for the users, and also give the sound of swinging rope synchronously, making it feels like swinging a real rope.
![](http://www.yejunli.com/wp-content/uploads/AirRopeJumping1-207x350.jpg)
[![](http://www.yejunli.com/wp-content/uploads/fake_video1.jpg)](https://youtu.be/h9ufiHwGIJM)
To prevent users from fabricating data by doing other motion, I tried machine learning method to detect cheating behaviors. Here is a demo:
[![](http://www.yejunli.com/wp-content/uploads/fake_video2.jpg)](https://youtu.be/-qjNP9J90ns)

# APPLE WATCH VERSION
I also develop a Apple Watch demo version. When users wear their apple watches to do rope jumping, the app will count for the users. Also, it can count the number of trips based on the interval time between jumps.
![](http://www.yejunli.com/wp-content/uploads/AirRopeJumping2-351x350.jpg)
[![](http://www.yejunli.com/wp-content/uploads/fake_video3.jpg)](https://youtu.be/D6CmfsiBunc)

# COUNTING ALGORITHM
The counting algorithm is based on the value of the vector product of acceleration and gravity. A jump is counted when the value exceed an upper threshold and then a lower threshold (a pair of red dots in the following figure).
![](http://www.yejunli.com/wp-content/uploads/AirRopeJumping4-624x350.png)

# CHEATING DECTECTION
I built a SVM classifier on the server using libSVM. The app sends the motion data to the server, and gets the classification results back. The accuracy is approximately 90%. Though 90% seems like a pretty good will result, the 10% misjudgment is terrible to users, so I didn’t put this feature into the release version.
![](http://www.yejunli.com/wp-content/uploads/AirRopeJumping3-768x453.png)

# OTHER INFORMATION
In the Android version, I didn't put the cheating detection function into the release version because the accuracy is not statle enough due to lack of data. So I share both the release version and cheating detection version here. 
I didn't publish the Apple Watch app, so you can't find it on App Store.
