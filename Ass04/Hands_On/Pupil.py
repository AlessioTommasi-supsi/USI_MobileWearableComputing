import cv2
import mediapipe as mp

# Initialize MediaPipe Iris solution
mp_face_mesh = mp.solutions.face_mesh
iris_tracking = mp_face_mesh.FaceMesh(refine_landmarks=True)

# Initialize webcam
cap = cv2.VideoCapture(0)

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    frame = cv2.flip(frame, 1)
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = iris_tracking.process(rgb_frame)

    if results.multi_face_landmarks:
        for face_landmarks in results.multi_face_landmarks:
            left_iris = face_landmarks.landmark[468]
            right_iris = face_landmarks.landmark[473]

            left_x, left_y = int(left_iris.x * frame.shape[1]), int(left_iris.y * frame.shape[0])
            right_x, right_y = int(right_iris.x * frame.shape[1]), int(right_iris.y * frame.shape[0])

            # Draw dots for left and right iris landmarks
            cv2.circle(frame, (left_x, left_y), 3, (0, 255, 0), -1)
            cv2.circle(frame, (right_x, right_y), 3, (0, 255, 0), -1)

    cv2.imshow('Iris Landmark Detection', frame)

    # Exit on 'q' key press
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
