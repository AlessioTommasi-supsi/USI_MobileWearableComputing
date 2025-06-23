import cv2
import mediapipe as mp
import numpy as np
from scipy.spatial import distance as dist
import time

# Initialize MediaPipe Face Mesh
mp_face_mesh = mp.solutions.face_mesh
face_mesh = mp_face_mesh.FaceMesh(static_image_mode=False, max_num_faces=1, refine_landmarks=True, min_detection_confidence=0.5, min_tracking_confidence=0.5)

# Initialize OpenCV
cap = cv2.VideoCapture(0)

def calculate_ear(eye):
    A = dist.euclidean(eye[1], eye[5])
    B = dist.euclidean(eye[2], eye[4])
    C = dist.euclidean(eye[0], eye[3])
    ear = (A + B) / (2.0 * C)
    return ear

# Eye indices based on MediaPipe Face Mesh
LEFT_EYE = [362, 385, 387, 263, 373, 380]
RIGHT_EYE = [33, 160, 158, 133, 153, 144]

def get_eye_landmarks(landmarks, eye_indices):
    return [(landmarks[i].x, landmarks[i].y) for i in eye_indices]

def calibrate_threshold(cap, face_mesh, duration=5):
    ear_values = []
    start_time = cv2.getTickCount()

    while (cv2.getTickCount() - start_time) / cv2.getTickFrequency() < duration:
        ret, frame = cap.read()
        if not ret:
            break

        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results = face_mesh.process(frame_rgb)

        if results.multi_face_landmarks:
            for face_landmarks in results.multi_face_landmarks:
                landmarks = face_landmarks.landmark
                left_eye_landmarks = get_eye_landmarks(landmarks, LEFT_EYE)
                right_eye_landmarks = get_eye_landmarks(landmarks, RIGHT_EYE)

                left_ear = calculate_ear(left_eye_landmarks)
                right_ear = calculate_ear(right_eye_landmarks)

                ear = (left_ear + right_ear) / 2.0
                ear_values.append(ear)

    threshold = np.mean(ear_values) - np.std(ear_values)
    return threshold

# Calibrate the blink detection threshold
blink_threshold = calibrate_threshold(cap, face_mesh)
print(f"Calibrated Blink Detection Threshold: {blink_threshold}")

# Initialize counters and timer
blink_count = 0
blink_cooldown = 0.5  # Cooldown period in seconds
last_blink_time = time.time()

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    blink_detected = False
    frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = face_mesh.process(frame_rgb)

    if results.multi_face_landmarks:
        for face_landmarks in results.multi_face_landmarks:
            landmarks = face_landmarks.landmark
            left_eye_landmarks = get_eye_landmarks(landmarks, LEFT_EYE)
            right_eye_landmarks = get_eye_landmarks(landmarks, RIGHT_EYE)

            left_ear = calculate_ear(left_eye_landmarks)
            right_ear = calculate_ear(right_eye_landmarks)

            ear = (left_ear + right_ear) / 2.0

            current_time = time.time()
            if ear < blink_threshold and (current_time - last_blink_time) > blink_cooldown:
                blink_count += 1
                last_blink_time = current_time
                blink_detected = True
                cv2.putText(frame, "Blink Detected", (50, 100), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)

    # Display blink count
    cv2.putText(frame, f"Blink Count: {blink_count}", (50, 150), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2, cv2.LINE_AA)

    cv2.imshow('Blink Detection', frame)

    key = cv2.waitKey(1) & 0xFF
    if key == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
