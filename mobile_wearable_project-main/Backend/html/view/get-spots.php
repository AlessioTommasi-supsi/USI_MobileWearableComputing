<?php
//Created and designed by Alessio Tommasi
header("Content-Type: application/json; charset=UTF-8");

include_once '../controller/Database.php';
include_once '../model/User.php';
include_once '../model/Post.php';


$latitude = isset($_REQUEST['latitude']) ? $_REQUEST['latitude'] : null;
$longitude = isset($_REQUEST['longitude']) ? $_REQUEST['longitude'] : null;

$database = new Database();
$db = $database->getConnection();

$posts = new Post($db);
$posts = $posts->get_spot();


header('Content-Type: application/json');
echo json_encode($posts);
?>