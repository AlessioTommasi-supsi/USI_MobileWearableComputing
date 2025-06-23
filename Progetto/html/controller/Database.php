<?php
class Database {
    private $host = "mysql_usi_mob_comp_project";
    private $db_name = "USI_MobileWearableProject";
    private $username = "root";
    private $password = "MYSQL_ROOT_PASSWORD";
    public $conn;

    public function getConnection() {
        $this->conn = null;

        try {
            $this->conn = new PDO("mysql:host={$this->host};dbname={$this->db_name}", $this->username, $this->password);
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch(PDOException $exception) {
            echo "Connection error: " . $exception->getMessage();
        }

        return $this->conn;
    }
}
?>