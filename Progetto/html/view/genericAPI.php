<?php
header("Content-Type: application/json; charset=UTF-8");

include_once '../controller/Database.php';
include_once '../model/User.php';
include_once '../model/Post.php';

$database = new Database();
$db = $database->getConnection();

$user = new User($db);
$post = new Post($db);

$data = [
    'users' => $user->getAllUsers(),
    'posts' => $post->getAllPosts()
];

echo json_encode($data);
?>