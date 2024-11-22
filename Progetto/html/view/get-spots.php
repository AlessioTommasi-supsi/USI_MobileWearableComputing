<?php
header("Content-Type: application/json; charset=UTF-8");

include_once '../controller/Database.php';
include_once '../model/User.php';
include_once '../model/Post.php';


$latitude = isset($_REQUEST['latitude']) ? $_REQUEST['latitude'] : null;
$longitude = isset($_REQUEST['longitude']) ? $_REQUEST['longitude'] : null;

$database = new Database();
$db = $database->getConnection();

$posts = new Post($db);
//$posts = $posts->getPosts($latitude, $longitude);
//$posts = $posts->getValidPosts($latitude, $longitude);
//$posts = $posts->getAllPosts();
//$posts = $posts->getDateChekedPosts();
$posts = $posts->get_spot();


header('Content-Type: application/json');
echo json_encode($posts);
?>