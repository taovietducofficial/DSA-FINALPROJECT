// Import các thư viện cần thiết
import javax.swing.*;          // Cho các thành phần giao diện cơ bản như JFrame, JPanel, JButton
import java.awt.*;            // Cho các thành phần đồ họa như Color, Font, Graphics
import java.io.BufferedReader; // Đọc file dữ liệu một cách hiệu quả
import java.io.FileReader;     // Đọc file văn bản
import java.io.IOException;    // Xử lý các ngoại lệ liên quan đến việc đọc/ghi file
import javax.imageio.ImageIO;  // Đọc và xử lý các file ảnh
import java.io.File;          // Xử lý file và thư mục

/**
 * Lớp Dashboard là màn hình chính của game Snake
 * Chứa menu chính với các nút điều khiển và hiển thị thông tin
 */
public class Dashboard {
    // Frame chính chứa toàn bộ giao diện game
    private static JFrame mainFrame; 

    /**
     * Phương thức main - điểm khởi đầu của chương trình
     */
    public static void main(String[] args) {
        // Khởi tạo cửa sổ chính của game với các thuộc tính cơ bản
        mainFrame = new JFrame("Snake Game - Adventure");
        mainFrame.setSize(800, 600);  // Đặt kích thước cửa sổ là 800x600 pixel
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát chương trình khi đóng cửa sổ
        mainFrame.setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình
        mainFrame.setResizable(false); // Không cho phép người dùng thay đổi kích thước cửa sổ

        // Tạo panel chính với hình nền
        JPanel panel = new JPanel() {
            // Biến lưu trữ hình ảnh nền
            private Image backgroundImage; 
            
            // Khối khởi tạo - chạy khi tạo đối tượng panel
            {
                try {
                    // Đọc file ảnh nền từ đường dẫn cụ thể
                    backgroundImage = ImageIO.read(new File("C:/DSA/images/istockphoto-1428272134-612x612.jpg"));
                } catch (IOException e) {
                    // In lỗi nếu không đọc được file ảnh
                    e.printStackTrace();
                }
            }
            
            // Ghi đè phương thức vẽ để hiển thị hình nền
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Vẽ hình nền trải đều theo kích thước panel
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        // Sử dụng BorderLayout để sắp xếp các thành phần theo vùng
        panel.setLayout(new BorderLayout()); 
        mainFrame.add(panel);

        // Thêm các thành phần giao diện vào panel
        placeComponents(panel);

        // Hiển thị cửa sổ
        mainFrame.setVisible(true);
    }

    /**
     * Phương thức đặt các thành phần giao diện lên panel chính
     * @param panel Panel chứa các thành phần giao diện
     */
    private static void placeComponents(JPanel panel) {
        // Tạo panel chứa logo và tiêu đề ở phía trên
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Làm cho panel trong suốt để thấy hình nền
        
        // Tạo nhãn tiêu đề game với font và màu sắc đặc biệt
        JLabel titleLabel = new JLabel("<html><font face='Press Start 2P' size='7' color='#FFD700'><b>SNAKE GAME</b></font></html>", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(255, 215, 0)); // Màu vàng gold cho tiêu đề
        titleLabel.setFont(new Font("Press Start 2P", Font.BOLD, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0)); // Tạo khoảng cách xung quanh tiêu đề
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Tạo panel chứa các nút điều khiển ở giữa màn hình
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Khoảng cách giữa các nút
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tạo các nút điều khiển với màu sắc khác nhau
        JButton startButton = createGameButton("START GAME", new Color(0, 200, 83)); // Nút màu xanh lá
        gbc.gridy = 0;
        buttonPanel.add(startButton, gbc);

        JButton scoresButton = createGameButton("HIGH SCORES", new Color(33, 150, 243)); // Nút màu xanh dương
        gbc.gridy = 1;
        buttonPanel.add(scoresButton, gbc);

        JButton settingsButton = createGameButton("SETTINGS", new Color(255, 152, 0)); // Nút màu cam
        gbc.gridy = 2;
        buttonPanel.add(settingsButton, gbc);

        JButton exitButton = createGameButton("EXIT", new Color(244, 67, 54)); // Nút màu đỏ
        gbc.gridy = 3;
        buttonPanel.add(exitButton, gbc);

        panel.add(buttonPanel, BorderLayout.CENTER);

        // Xử lý sự kiện khi nhấn nút Start Game
        startButton.addActionListener(e -> {
            try {
                mainFrame.setVisible(false); // Ẩn màn hình chính
                // Tạo cửa sổ mới cho game
                JFrame gameFrame = new JFrame("Snake Game");
                gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gameFrame.add(new EnhancedSnakeGame());
                gameFrame.pack();
                gameFrame.setLocationRelativeTo(null);
                // Thêm sự kiện khi đóng cửa sổ game
                gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        mainFrame.setVisible(true); // Hiện lại màn hình chính
                    }
                });
                gameFrame.setVisible(true);
            } catch (Exception ex) {
                showErrorDialog("Could not start the game!");
                mainFrame.setVisible(true);
            }
        });

        // Xử lý sự kiện khi nhấn nút High Scores
        scoresButton.addActionListener(e -> {
            try {
                // Đọc điểm cao từ file
                BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"));
                String highScore = reader.readLine();
                reader.close();

                // Tạo và hiển thị dialog điểm cao
                JDialog scoreDialog = new JDialog();
                scoreDialog.setTitle("High Score");
                scoreDialog.setSize(300, 150);
                scoreDialog.setLocationRelativeTo(null);
                scoreDialog.setModal(true);

                // Panel chứa thông tin điểm cao
                JPanel scorePanel = new JPanel(new BorderLayout());
                scorePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                // Nhãn hiển thị điểm cao
                JLabel scoreLabel = new JLabel("High Score: " + highScore, SwingConstants.CENTER);
                scoreLabel.setFont(new Font("Press Start 2P", Font.BOLD, 20));
                scoreLabel.setForeground(new Color(255, 215, 0));

                scorePanel.add(scoreLabel, BorderLayout.CENTER);
                scoreDialog.add(scorePanel);
                scoreDialog.setVisible(true);

            } catch (IOException ex) {
                showErrorDialog("Could not read high score!");
            }
        });

        // Xử lý sự kiện khi nhấn nút Exit
        exitButton.addActionListener(e -> System.exit(0)); // Thoát chương trình

        // Tạo panel footer chứa thông tin tác giả
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel versionLabel = new JLabel("TÀO VIỆT ĐỨC - MSSV: 21110169", SwingConstants.CENTER);
        versionLabel.setForeground(new Color(158, 158, 158)); // Màu xám cho chữ
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        footerPanel.add(versionLabel);
        
        panel.add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Phương thức tạo nút game với style thống nhất
     * @param text Chữ hiển thị trên nút
     * @param baseColor Màu nền của nút
     * @return JButton đã được tùy chỉnh
     */
    private static JButton createGameButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50)); // Kích thước nút
        button.setFont(new Font("Press Start 2P", Font.BOLD, 16)); // Font chữ kiểu game
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE); // Màu chữ trắng
        button.setFocusPainted(false); // Bỏ viền focus
        button.setBorderPainted(false); // Bỏ viền nút
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Con trỏ chuột kiểu bàn tay

        // Thêm hiệu ứng hover khi di chuột qua nút
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter()); // Làm sáng màu nút
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor); // Trở về màu ban đầu
            }
        });

        return button;
    }

    /**
     * Phương thức hiển thị dialog thông báo lỗi
     * @param message Nội dung thông báo lỗi
     */
    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
