import cv2
import numpy as np
import mediapipe as mp
import time

# Define function to draw the nose tip on the canvas with coordinates
def draw_nose_tip_with_coords(canvas, nose_tip, coords, thickness=2, color=(0, 0, 0), text_color=(0, 0, 0)):
    canvas_out = canvas.copy()
    cv2.circle(canvas_out, (int(nose_tip[0]), int(nose_tip[1])), 5, color, thickness)
    cv2.putText(canvas_out, f"({int(coords[0])}, {int(coords[1])})", 
                (int(nose_tip[0]) + 10, int(nose_tip[1]) - 10), 
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, text_color, 1, cv2.LINE_AA)
    return canvas_out

# Define function to draw quadrants on the canvas
def draw_quadrants(canvas, screen_width, screen_height):
    quadrant_width = screen_width // 3
    quadrant_height = screen_height // 3
    canvas_out = canvas.copy()
    for i in range(1, 3):
        # Draw vertical lines
        cv2.line(canvas_out, (i * quadrant_width, 0), (i * quadrant_width, screen_height), (0, 0, 0), 2)
        # Draw horizontal lines
        cv2.line(canvas_out, (0, i * quadrant_height), (screen_width, i * quadrant_height), (0, 0, 0), 2)
    return canvas_out

# Define function to blink the quadrant where the nose tip is located
def blink_quadrant(canvas, screen_x, screen_y, screen_width, screen_height, blink_color=(0, 0, 255)):
    quadrant_width = screen_width // 3
    quadrant_height = screen_height // 3

    x_index = int(screen_x // quadrant_width)
    y_index = int(screen_y // quadrant_height)

    x_start = x_index * quadrant_width
    y_start = y_index * quadrant_height

    canvas_out = canvas.copy()
    cv2.rectangle(canvas_out, (x_start, y_start), (x_start + quadrant_width, y_start + quadrant_height), blink_color, -1)
    return canvas_out

if __name__ == '__main__':
    # Initialize camera capture
    cap = cv2.VideoCapture(0)

    # Initialize MediaPipe Face Mesh
    mp_face_mesh = mp.solutions.face_mesh
    face_mesh = mp_face_mesh.FaceMesh()
    
    # Define screen size
    screen_width, screen_height = 1920, 1080
    
    # Define size for the small frame (real-time face display)
    small_frame_width = 320
    small_frame_height = 240

    # Create a white canvas of the same size as the screen
    canvas = np.ones((screen_height, screen_width, 3), dtype=np.uint8) * 255  # White canvas

    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break
        
        # Resize the frame for the small display
        small_frame = cv2.resize(frame, (small_frame_width, small_frame_height))

        # Convert BGR image to RGB
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        
        # Perform face mesh detection
        results = face_mesh.process(rgb_frame)
        
        # Clear canvas (optional: only if you want to reset the canvas each frame)
        canvas[:] = 255  # Reset to white

        # Draw quadrants on canvas
        canvas = draw_quadrants(canvas, screen_width, screen_height)

        if results.multi_face_landmarks:
            for face_landmarks in results.multi_face_landmarks:
                # Get nose tip coordinates (index 1, which may vary)
                nose_tip = face_landmarks.landmark[1]
                nose_tip_x = int(nose_tip.x * frame.shape[1])
                nose_tip_y = int(nose_tip.y * frame.shape[0])
                
                # Convert nose tip coordinates to canvas coordinates and invert x-axis for correct movement
                screen_x = (1 - nose_tip.x) * screen_width
                screen_y = nose_tip.y * screen_height

                # Blink the quadrant where the nose tip is located
                canvas = blink_quadrant(canvas, screen_x, screen_y, screen_width, screen_height)
                
                # Draw the nose tip and screen coordinates on the canvas
                canvas = draw_nose_tip_with_coords(canvas, (screen_x, screen_y), (screen_x, screen_y))

        # Place the small frame (real-time face display) on the top left corner of the canvas
        canvas[0:small_frame_height, 0:small_frame_width] = small_frame

        # Display the canvas with nose tip, coordinates, quadrants, and small frame
        cv2.imshow('Canvas', canvas)

        # Check for 'q' key press to exit
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # Release capture and close windows
    cap.release()
    cv2.destroyAllWindows()
