import cv2
import mediapipe as mp

# Initialize MediaPipe Face Mesh
mp_face_mesh = mp.solutions.face_mesh
mp_drawing = mp.solutions.drawing_utils

# Initialize webcam
cap = cv2.VideoCapture(0)

# MediaPipe face mesh configuration
with mp_face_mesh.FaceMesh(min_detection_confidence=0.5, min_tracking_confidence=0.5) as face_mesh:
    while cap.isOpened():
        success, frame = cap.read()
        if not success:
            print("Ignoring empty camera frame.")
            continue

        # Convert the BGR frame to RGB
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

        # Process the frame with MediaPipe Face Mesh (to get detailed landmarks)
        results = face_mesh.process(rgb_frame)

        # Draw the face bounding boxes on the frame
        if results.multi_face_landmarks:
            for landmarks in results.multi_face_landmarks:
                # Iterate over all face landmarks with their index
                for idx, landmark in enumerate(landmarks.landmark):
                    # Get coordinates of the landmark
                    x = int(landmark.x * frame.shape[1])
                    y = int(landmark.y * frame.shape[0])

                    # Filter out ear and lip landmarks 
                    if not (61 <= idx <= 91 or 234 <= idx <= 243):
                        
                        cv2.circle(frame, (x, y), 1, (255, 200, 200), 1)  

        # Display the frame with annotations
        cv2.imshow('Face Detection and Mesh', frame)

        # Exit the loop if 'q' is pressed
        if cv2.waitKey(5) & 0xFF == ord('q'):
            break

# Release the webcam and close windows
cap.release()
cv2.destroyAllWindows()
