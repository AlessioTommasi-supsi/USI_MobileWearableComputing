<?php
include_once '../controller/Database.php';

class Post {
    private $conn;
    private $table_name = "Post";

    public function __construct($db) {
        $this->conn = $db;
    }

    public function getAllPosts() {
        $query = "SELECT * FROM " . $this->table_name;
        $stmt = $this->conn->prepare($query);
        $stmt->execute();

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function getPostById($id) {
        $query = "SELECT * FROM " . $this->table_name . " WHERE id = :id";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    public function getPosts($latitude = null, $longitude = null){
        if ($latitude !== null && $longitude !== null) {
            $query = "SELECT * FROM " . $this->table_name . " WHERE ST_Distance_Sphere(
                        point(longitude, latitude),
                        point(:longitude, :latitude)
                      ) <= visibility_radius";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':latitude', $latitude);
            $stmt->bindParam(':longitude', $longitude);
        } else {
            $query = "SELECT * FROM " . $this->table_name;
            $stmt = $this->conn->prepare($query);
        }

        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

}
?>