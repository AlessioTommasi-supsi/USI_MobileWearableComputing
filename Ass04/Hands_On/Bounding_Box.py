import cv2
import mediapipe as mp

# Initialize MediaPipe Face Detection
mp_face_detection = mp.solutions.face_detection
mp_drawing = mp.solutions.drawing_utils

# Function for face detection
def detect_faces(source, is_webcam=False):
    with mp_face_detection.FaceDetection(min_detection_confidence=0.1) as face_detection:
        cap = None

        # Handle different sources
        if is_webcam:
            cap = cv2.VideoCapture(source)  # For webcam (source = 0)
        elif source.endswith(('.png', '.jpg', '.jpeg')):  # For images
            frame = cv2.imread(source)
            process_frame(frame, face_detection)
            cv2.waitKey(0)
            cv2.destroyAllWindows()
            return
        else:  # For video files
            cap = cv2.VideoCapture(source)
        
        # For video or webcam feed
        if cap:
            while cap.isOpened():
                ret, frame = cap.read()
                if not ret:
                    print("No more frames in the video")
                    break
                process_frame(frame, face_detection)
                
                # Check for 'q' key to exit
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    print("Exiting on user request")
                    break

            cap.release()
            cv2.destroyAllWindows()

# Function to process and draw bounding boxes
def process_frame(frame, face_detection):
    frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = face_detection.process(frame_rgb)

    # Draw bounding boxes if faces are detected
    if results.detections:
        for detection in results.detections:
            bboxC = detection.location_data.relative_bounding_box
            ih, iw, _ = frame.shape
            x, y, w, h = int(bboxC.xmin * iw), int(bboxC.ymin * ih), \
                         int(bboxC.width * iw), int(bboxC.height * ih)
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
    cv2.imshow('Face Detection', frame)

# For real-time webcam feed
#detect_faces(0, is_webcam=True)

# For detecting faces in an image file
#detect_faces('face.jpeg')

# For detecting faces in a video file
#detect_faces('Video1.mp4')
