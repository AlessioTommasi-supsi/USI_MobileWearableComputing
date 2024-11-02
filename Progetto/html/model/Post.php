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
        $responce = array();
        if ($latitude !== null && $longitude !== null ) {
            $query = "SELECT * FROM " . $this->table_name . ",
                     gps
                    WHERE 
                    gps.id = Post.fk_location AND
                    ST_Distance_Sphere(
                        point(gps.longitude, gps.latitude),
                        point(:longitude, :latitude)
                      ) <= visibility_radius";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':latitude', $latitude);
            $stmt->bindParam(':longitude', $longitude);
            $stmt->execute();
            $responce = $stmt->fetchAll(PDO::FETCH_ASSOC);

        } else {
            //$responce = $this->getAllPosts();
            $responce = array("error" => "Invalid or missing coordinates");
        }

        
        return $responce;
    }

    public function getValidPosts($latitude = null, $longitude = null) {
        $response = array();
        $currentDateTime = date('Y-m-d H:i:s');
        
        if ($latitude !== null && $longitude !== null) {
            $query = "SELECT * FROM " . $this->table_name . ",
                     gps
                    WHERE 
                    gps.id = Post.fk_location AND
                    ST_Distance_Sphere(
                        point(gps.longitude, gps.latitude),
                        point(:longitude, :latitude)
                      ) <= visibility_radius AND
                    Post.expirationDateTime > :currentDateTime";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':latitude', $latitude);
            $stmt->bindParam(':longitude', $longitude);
            $stmt->bindParam(':currentDateTime', $currentDateTime);
            $stmt->execute();
            $response = $stmt->fetchAll(PDO::FETCH_ASSOC);

        } else {
            $response = array("error" => "Invalid or missing coordinates");
        }

        return $response;
    }

}
?>