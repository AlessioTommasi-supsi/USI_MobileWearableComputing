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
}
?>