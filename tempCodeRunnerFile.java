import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;

public class Dashboard {
    public static void main(String[] args) {
        // Tạo frame chính với theme game
        JFrame frame = new JFrame("Snake Game - Adventure");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Tạo panel chính với background image
        JPanel panel = new JPanel() {
            private Image backgroundImage;
            
            {
                try {
                    backgroundImage = ImageIO.read(new File("C:/DSA/images/istockphoto-1428272134-612x612.jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new BorderLayout());
        frame.add(panel);

        // Đặt các thành phần vào panel
        placeComponents(panel);

        // Hiển thị frame
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        // Panel chứa logo và tiêu đề
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Tiêu đề game với hiệu ứng glow
        JLabel titleLabel = new JLabel("SNAKE ADVENTURE", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(255, 215, 0)); // Màu vàng gold
        titleLabel.setFont(new Font("Press Start 2P", Font.BOLD, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Panel chứa các nút điều khiển
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nút Start Game với hiệu ứng hover
        JButton startButton = createGameButton("START GAME", new Color(0, 200, 83));
        gbc.gridy = 0;
        buttonPanel.add(startButton, gbc);

        // Nút High Scores
        JButton scoresButton = createGameButton("HIGH SCORES", new Color(33, 150, 243));
        gbc.gridy = 1;
        buttonPanel.add(scoresButton, gbc);

        // Nút Settings
        JButton settingsButton = createGameButton("SETTINGS", new Color(255, 152, 0));
        gbc.gridy = 2;
        buttonPanel.add(settingsButton, gbc);

        // Nút Exit
        JButton exitButton = createGameButton("EXIT", new Color(244, 67, 54));
        gbc.gridy = 3;
        buttonPanel.add(exitButton, gbc);

        panel.add(buttonPanel, BorderLayout.CENTER);

        // Thêm sự kiện cho các nút
        startButton.addActionListener(e -> {
            try {
                JFrame gameFrame = new JFrame("Snake Game");
                gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gameFrame.add(new EnhancedSnakeGame());
                gameFrame.pack();
                gameFrame.setLocationRelativeTo(null);
                gameFrame.setVisible(true);
            } catch (Exception ex) {
                showErrorDialog("Could not start the game!");
            }
        });

        scoresButton.addActionListener(e -> {
            try {
                // Đọc điểm cao từ file
                BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"));
                String highScore = reader.readLine();
                reader.close();

                // Tạo dialog hiển thị điểm cao
                JDialog scoreDialog = new JDialog();
                scoreDialog.setTitle("High Score");
                scoreDialog.setSize(300, 150);
                scoreDialog.setLocationRelativeTo(null);
                scoreDialog.setModal(true);

                JPanel scorePanel = new JPanel(new BorderLayout());
                scorePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        exitButton.addActionListener(e -> System.exit(0));

        // Footer với thông tin phiên bản
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel versionLabel = new JLabel("TÀO VIỆT ĐỨC - MSSV: 21110169", SwingConstants.CENTER);
        versionLabel.setForeground(new Color(158, 158, 158));
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        footerPanel.add(versionLabel);
        
        panel.add(footerPanel, BorderLayout.SOUTH);
    }

    private static JButton createGameButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Press Start 2P", Font.BOLD, 16));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
