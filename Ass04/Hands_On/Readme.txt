ENVIRONMENT SETUP

conda create --name tutorial python=3.10
conda activate tutorial  
cd to/path/where/file/is
pip install -r requirements.txt


This folder has following codes:
1.Bounding_Box.py
2.Face_Mesh.py
3.Blink.py
4.Pupil.py
5.Screen.py

There is 1 image and 2 video files for Code no. 1
-> For this code comment out any of the 3 conditions below as per the requirement of implementation

# For real-time webcam feed 
#detect_faces(0, is_webcam=True) 

# For detecting faces in an image file 
#detect_faces('face.jpeg’)

# For detecting faces in a video file 
#detect_faces('Video1.mp4')
