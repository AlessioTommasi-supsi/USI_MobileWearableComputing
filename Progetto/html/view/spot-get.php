<?php
header("Content-Type: application/json; charset=UTF-8");

include_once '../controller/Database.php';
include_once '../model/User.php';
include_once '../model/Post.php';


$message = isset($_REQUEST['message']) ? $_REQUEST['message'] : null;
$imageString = isset($_REQUEST['imageString']) ? $_REQUEST['imageString'] : null;
$attachmentType = "IMAGE";
$latitude = isset($_REQUEST['latitude']) ? $_REQUEST['latitude'] : null;
$longitude = isset($_REQUEST['longitude']) ? $_REQUEST['longitude'] : null;
$visibilityRadius = isset($_REQUEST['visibilityRangeRadiusInMeters']) ? $_REQUEST['visibilityRangeRadiusInMeters'] : null;
$expirationDateTime = isset($_REQUEST['expirationDateTime']) ? $_REQUEST['expirationDateTime'] : null;


//$posts = $posts->getPosts($latitude, $longitude);
//$posts = $posts->getValidPosts($latitude, $longitude);
//$posts = $posts->getAllPosts();
/*
$posts = $posts->getDateChekedPosts();

header('Content-Type: application/json');
echo json_encode($posts);
*/

if ($message !== null && $imageString !== null && $latitude !== null && $longitude !== null && $visibilityRadius !== null && $expirationDateTime !== null) {
    $database = new Database();
    $db = $database->getConnection();

    $posts = new Post($db);

    $result =$posts->addSpot($message, $imageString, $attachmentType, $latitude, $longitude, $visibilityRadius, $expirationDateTime);
    if ($result) {
        $response = array("success" => "Spot added successfully");
    } else {
        $response = array("error" => "Failed to add spot: " . $result);
    }
} else {
    $response = array("error" => "Invalid or missing parameters");
}

echo json_encode($response);

?>