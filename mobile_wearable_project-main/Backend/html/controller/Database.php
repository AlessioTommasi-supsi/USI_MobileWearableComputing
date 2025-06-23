<?php

class Database {
    // // This function was adopted from generative AI. (2021). How to Connect to a MySQL Database Using PHP. [online] Available at: https://www.generativeai.com/how-to-connect-to-a-mysql-database-using-php/ [Accessed 1 May 2021].
    private $host = "mysql_usi_mob_comp_project";
    private $db_name = "USI_MobileWearableProject";
    private $username = "root";
    private $password = "INSERT_HERE_YOUR_PASSWORD";
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