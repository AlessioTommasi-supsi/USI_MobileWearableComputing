<?php
header("Content-Type: application/json; charset=UTF-8");

include_once '../controller/Database.php';
include_once '../model/User.php';
include_once '../model/Post.php';

$database = new Database();
$db = $database->getConnection();

$posts = new Post($db);

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);

    if ($data) {
        // Use JSON request body
        $message = isset($data['message']) ? $data['message'] : null;
        $imageString = isset($data['imageString']) ? $data['imageString'] : null;
        $attachmentType = "IMAGE";
        $latitude = isset($data['latitude']) ? $data['latitude'] : null;
        $longitude = isset($data['longitude']) ? $data['longitude'] : null;
        $visibilityRadius = isset($data['visibilityRangeRadiusInMeters']) ? $data['visibilityRangeRadiusInMeters'] : null;
        $expirationDateTime = isset($data['expirationDateTime']) ? $data['expirationDateTime'] : null;
    } else {
        // Use URL parameters
        $message = isset($_REQUEST['message']) ? $_REQUEST['message'] : null;
        $imageString = isset($_REQUEST['imageString']) ? $_REQUEST['imageString'] : null;
        $attachmentType = "IMAGE";
        $latitude = isset($_REQUEST['latitude']) ? $_REQUEST['latitude'] : null;
        $longitude = isset($_REQUEST['longitude']) ? $_REQUEST['longitude'] : null;
        $visibilityRadius = isset($_REQUEST['visibilityRangeRadiusInMeters']) ? $_REQUEST['visibilityRangeRadiusInMeters'] : null;
        $expirationDateTime = isset($_REQUEST['expirationDateTime']) ? $_REQUEST['expirationDateTime'] : null;
    }

    if ($message && $imageString && $latitude && $longitude && $visibilityRadius && $expirationDateTime) {
        $result = $posts->addSpot($message, $imageString, $attachmentType, $latitude, $longitude, $visibilityRadius, $expirationDateTime);
        if ($result) {
            http_response_code(200);
            echo json_encode(array("message" => "Spot added successfully."));
        } else {
            http_response_code(400);
            echo json_encode(array("message" => "Unable to add spot."));
        }
    } else {
        http_response_code(400);
        echo json_encode(array("message" => "Invalid input."));
    }
} elseif ($method == 'GET') {
    $result = $posts->get_spot();
    echo json_encode($result);
} else {
    http_response_code(405);
    echo json_encode(array("message" => "Method not allowed."));
}
?>

?>