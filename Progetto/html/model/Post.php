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
            //$responce = array("error" => "Invalid or missing coordinates");
            $query = "SELECT * FROM " . $this->table_name . ",
                     gps
                    WHERE 
                    gps.id = Post.fk_location
                    ";
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            $responce = $stmt->fetchAll(PDO::FETCH_ASSOC);
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
            //$response = array("error" => "Invalid or missing coordinates");
            $query = "SELECT * FROM " . $this->table_name;
            echo $query;
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            $responce = $stmt->fetchAll(PDO::FETCH_ASSOC);
        }

        return $response;
    }

    public function getDateChekedPosts() {
        $response = array();
        $currentDateTime = date('Y-m-d H:i:s');
        $query = "SELECT * FROM " . $this->table_name . " WHERE expirationDateTime > :currentDateTime";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(':currentDateTime', $currentDateTime);
        $stmt->execute();
        $response = $stmt->fetchAll(PDO::FETCH_ASSOC);

        return $response;
    }

    public function get_spot() {
        $query = "SELECT 
                    Post.id, 
                    Post.message, 
                    Attachment.url AS imageString, 
                    gps.longitude, 
                    gps.latitude, 
                    Post.visibility_radius AS visibilityRangeRadiusInMeters, 
                    Post.expirationDateTime 
                  FROM " . $this->table_name . "
                  JOIN gps ON gps.id = Post.fk_location
                  JOIN Attachment ON Attachment.id = Post.fk_attachment";
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
    
        $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
        return $result;
    }

    public function addSpot($message, $attachment_url, $attachment_type, $latitude, $longitude, $visibility_radius, $expirationDateTime, $creator_id = 1/*se non messo 1 e user di default */) {
        try {
            // Begin transaction
            $this->conn->beginTransaction();
            
    
            // Insert into gps table
            $query = "INSERT INTO gps (latitude, longitude) VALUES (:latitude, :longitude)";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':latitude', $latitude);
            $stmt->bindParam(':longitude', $longitude);
            $stmt->execute();
            $fk_location = $this->conn->lastInsertId();
    
            // Insert into Attachment table
            $query = "INSERT INTO Attachment (url, type) VALUES (:url, :type)";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':url', $attachment_url);
            $stmt->bindParam(':type', $attachment_type);
            $stmt->execute();
            $fk_attachment = $this->conn->lastInsertId();
    
            // Insert into Post table
            $query = "INSERT INTO " . $this->table_name . " (message, fk_location, fk_attachment, fk_creator, visibility_radius, expirationDateTime) VALUES (:message, :fk_location, :fk_attachment, :fk_creator, :visibility_radius, :expirationDateTime)";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(':message', $message);
            $stmt->bindParam(':fk_location', $fk_location);
            $stmt->bindParam(':fk_attachment', $fk_attachment);
            $stmt->bindParam(':fk_creator', $creator_id);
            $stmt->bindParam(':visibility_radius', $visibility_radius);
            $stmt->bindParam(':expirationDateTime', $expirationDateTime);
            $stmt->execute();
    
            // Commit transaction
            $this->conn->commit();
            return true;
        } catch (Exception $e) {
            echo $e->getMessage();
            // Rollback transaction if something failed
            $this->conn->rollBack();
            return false;
        }
    }

}