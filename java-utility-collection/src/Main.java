public class Main {
    public static void main(String[] args) {
        try {
            String password = "MySecretPassword!";
            
            // Encrypt
            String encryptedPassword = AESUtil.encrypt(password);
            System.out.println("Encrypted Password: " + encryptedPassword);

            // Decrypt
            String decryptedPassword = AESUtil.decrypt(encryptedPassword);
            System.out.println("Decrypted Password: " + decryptedPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
