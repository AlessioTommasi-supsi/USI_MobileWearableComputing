<?php
require_once '../config/database.php'; // Adjust the path as necessary

function getPosts($curr_gps_location, $latitude = null, $longitude = null) {
    $db = new Database();
    $conn = $db->getConnection();

    if ($latitude !== null && $longitude !== null) {
        $query = "SELECT * FROM Posts WHERE ST_Distance_Sphere(
                    point(longitude, latitude),
                    point(:longitude, :latitude)
                  ) <= visibility_radius";
        $stmt = $conn->prepare($query);
        $stmt->bindParam(':latitude', $latitude);
        $stmt->bindParam(':longitude', $longitude);
    } else {
        $query = "SELECT * FROM posts";
        $stmt = $conn->prepare($query);
    }

    $stmt->execute();
    $posts = $stmt->fetchAll(PDO::FETCH_ASSOC);

    return $posts;
}

$latitude = isset($_REQUEST['curr_gps_location']) ? $_REQUEST['curr_gps_location'] : null;
$latitude = isset($_REQUEST['latitude']) ? $_REQUEST['latitude'] : null;
$longitude = isset($_REQUEST['longitude']) ? $_REQUEST['longitude'] : null;

$posts = getPosts( $curr_gps_location, $latitude, $longitude);

header('Content-Type: application/json');
echo json_encode($posts);
?>