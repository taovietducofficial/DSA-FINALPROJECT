// Import các thư viện cần thiết cho game
import javax.swing.*;          // Thư viện cho giao diện đồ họa cơ bản
import java.awt.*;            // Thư viện cho đồ họa và giao diện người dùng
import java.awt.event.*;      // Thư viện xử lý sự kiện
import java.util.LinkedList;  // Cấu trúc dữ liệu danh sách liên kết để lưu thân rắn
import java.util.Random;      // Thư viện tạo số ngẫu nhiên
import java.io.*;            // Thư viện đọc/ghi file
import java.util.logging.*;  // Thư viện ghi log
import javax.imageio.ImageIO; // Thư viện đọc file ảnh

// Lớp chính của game rắn săn mồi
// Kế thừa từ JPanel để vẽ giao diện
// Thực thi ActionListener để xử lý sự kiện timer
// Thực thi KeyListener để xử lý sự kiện bàn phím
public class EnhancedSnakeGame extends JPanel implements ActionListener, KeyListener {
    // Khởi tạo logger để ghi lại các sự kiện và lỗi trong game
    private static final Logger LOGGER = Logger.getLogger(EnhancedSnakeGame.class.getName());

    // Các thông số cấu hình cơ bản của game
    private final int TILE_SIZE = 25;     // Kích thước mỗi ô vuông trên lưới (pixel)
    private final int GRID_WIDTH = 30;    // Số ô theo chiều rộng của lưới game
    private final int GRID_HEIGHT = 20;   // Số ô theo chiều cao của lưới game
    private final int INITIAL_SPEED = 120; // Tốc độ ban đầu của rắn (ms)
    private final int SPEED_INCREMENT = 5; // Mức tăng tốc độ mỗi khi lên cấp
    private final int INITIAL_OBSTACLES = 5; // Số chướng ngại vật ban đầu
    private final int OBSTACLES_INCREMENT = 3; // Số chướng ngại vật tăng thêm mỗi cấp
    private final int INFO_PANEL_WIDTH = 200; // Chiều rộng của bảng thông tin bên phải

    // Các biến quản lý trạng thái game
    private LinkedList<Point> snake;      // Danh sách các điểm tạo thành thân rắn
    private Point food;                   // Vị trí của thức ăn
    private String direction = "RIGHT";   // Hướng di chuyển hiện tại của rắn
    private boolean gameRunning = true;   // Trạng thái game (đang chạy/kết thúc)
    private boolean isPaused = false;     // Trạng thái tạm dừng
    private Timer timer;                  // Bộ đếm thời gian để cập nhật game
    private int score = 0;               // Điểm số hiện tại
    private int highScore = 0;           // Điểm cao nhất đạt được
    private int level = 1;               // Cấp độ hiện tại
    private LinkedList<Point> obstacles = new LinkedList<>(); // Danh sách vị trí chướng ngại vật
    private int currentSpeed = INITIAL_SPEED; // Tốc độ di chuyển hiện tại của rắn

    // Các tài nguyên hình ảnh của game
    private Image snakeHeadImage;    // Hình ảnh đầu rắn
    private Image snakeBodyImage;    // Hình ảnh thân rắn
    private Image foodImage;         // Hình ảnh thức ăn
    private Image obstacleImage;     // Hình ảnh chướng ngại vật
    private Image backgroundImage;   // Hình ảnh nền game
    private final Font infoFont = new Font("Arial", Font.BOLD, 18);    // Font chữ cho thông tin
    private final Font gameOverFont = new Font("Arial", Font.BOLD, 36); // Font chữ cho màn hình kết thúc

    // Các nút điều khiển tốc độ
    private JButton speedUpButton;    // Nút tăng tốc độ
    private JButton speedDownButton;  // Nút giảm tốc độ

    // Constructor khởi tạo game
    public EnhancedSnakeGame() {
        setLayout(null); // Sử dụng null layout để tự do đặt vị trí các thành phần
        
        // Thiết lập kích thước và thuộc tính của panel game
        this.setPreferredSize(new Dimension(TILE_SIZE * GRID_WIDTH + INFO_PANEL_WIDTH, TILE_SIZE * GRID_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        // Khởi tạo các thành phần của game
        loadImages();    // Tải các hình ảnh cần thiết
        loadHighScore(); // Tải điểm cao từ file

        // Khởi tạo timer và bắt đầu game
        timer = new Timer(INITIAL_SPEED, this);
        initGame();
        timer.start();
    }

    // Phương thức điều chỉnh tốc độ game
    private void adjustSpeed(int change) {
        // Giới hạn tốc độ trong khoảng 50-200ms
        currentSpeed = Math.max(50, Math.min(200, currentSpeed + change));
        timer.setDelay(currentSpeed);
        requestFocusInWindow(); // Lấy lại focus cho panel sau khi nhấn nút
    }

    // Phương thức tải các hình ảnh game
    private void loadImages() {
        try {
            // Tải các file ảnh từ thư mục
            snakeHeadImage = ImageIO.read(new File("C:/DSA/images/head_down.png"));
            snakeBodyImage = ImageIO.read(new File("C:/DSA/images/body_horizontal.png")); 
            foodImage = ImageIO.read(new File("C:/DSA/images/apple.png"));
            obstacleImage = ImageIO.read(new File("C:/DSA/images/pngtree-roadblock-obstacle-png-image_6695891.jpg"));
            backgroundImage = ImageIO.read(new File("C:/DSA/images/istockphoto-1428272134-612x612.jpg"));
        } catch (IOException e) {
            // Ghi log nếu không tải được ảnh
            LOGGER.log(Level.SEVERE, "Could not load game images", e);
        }
    }

    // Phương thức khởi tạo trạng thái ban đầu của game
    private void initGame() {
        // Khởi tạo con rắn với 3 đốt ban đầu
        snake = new LinkedList<>();
        snake.add(new Point(5, 5)); // Đầu rắn
        snake.add(new Point(4, 5)); // Thân rắn
        snake.add(new Point(3, 5)); // Đuôi rắn

        // Tạo thức ăn và chướng ngại vật
        spawnFood();
        spawnObstacles();

        // Thiết lập các giá trị ban đầu
        score = 0;
        level = 1;
        gameRunning = true;
        direction = "RIGHT";
        currentSpeed = INITIAL_SPEED;
        timer.setDelay(currentSpeed);
    }

    // Phương thức tạo thức ăn mới
    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        // Tạo vị trí ngẫu nhiên cho thức ăn cho đến khi tìm được vị trí hợp lệ
        do {
            x = rand.nextInt(GRID_WIDTH);
            y = rand.nextInt(GRID_HEIGHT);
            food = new Point(x, y);
        } while (snake.contains(food) || obstacles.contains(food)); // Đảm bảo thức ăn không trùng với rắn hoặc chướng ngại vật
    }

    // Phương thức tạo chướng ngại vật
    private void spawnObstacles() {
        Random rand = new Random();
        obstacles.clear(); // Xóa các chướng ngại vật cũ
        
        // Tính số lượng chướng ngại vật dựa trên cấp độ
        int obstacleCount = INITIAL_OBSTACLES + (level - 1) * OBSTACLES_INCREMENT;
        // Giới hạn số lượng chướng ngại vật không quá 40% diện tích màn hình
        int maxObstacles = (int)(GRID_WIDTH * GRID_HEIGHT * 0.4);
        obstacleCount = Math.min(obstacleCount, maxObstacles);

        // Tạo các chướng ngại vật
        for (int i = 0; i < obstacleCount; i++) {
            Point obstacle;
            do {
                obstacle = new Point(rand.nextInt(GRID_WIDTH), rand.nextInt(GRID_HEIGHT));
            } while (snake.contains(obstacle) || obstacle.equals(food) || isNearSnakeHead(obstacle));
            obstacles.add(obstacle);
        }
    }

    // Kiểm tra xem một điểm có gần đầu rắn không
    private boolean isNearSnakeHead(Point obstacle) {
        Point head = snake.getFirst();
        // Tính khoảng cách Manhattan giữa đầu rắn và chướng ngại vật
        int distance = Math.abs(head.x - obstacle.x) + Math.abs(head.y - obstacle.y);
        return distance <= 2; // Trả về true nếu khoảng cách <= 2
    }

    // Phương thức vẽ các thành phần của game
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Bật chế độ làm mịn để hình ảnh đẹp hơn
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ các thành phần của game
        g2d.drawImage(backgroundImage, 0, 0, TILE_SIZE * GRID_WIDTH, TILE_SIZE * GRID_HEIGHT, this);
        drawSnake(g2d);
        drawFood(g2d);
        drawObstacles(g2d);
        drawInfoPanel(g2d);

        // Vẽ màn hình game over nếu game kết thúc
        if (!gameRunning) drawGameOver(g2d);
    }

    // Phương thức vẽ con rắn
    private void drawSnake(Graphics2D g2d) {
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            // Sử dụng hình ảnh đầu rắn cho phần đầu, thân rắn cho các phần còn lại
            Image snakeImage = i == 0 ? snakeHeadImage : snakeBodyImage;
            g2d.drawImage(snakeImage, p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        }
    }

    // Phương thức vẽ thức ăn
    private void drawFood(Graphics2D g2d) {
        g2d.drawImage(foodImage, food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
    }

    // Phương thức vẽ chướng ngại vật
    private void drawObstacles(Graphics2D g2d) {
        for (Point obstacle : obstacles) {
            g2d.drawImage(obstacleImage, obstacle.x * TILE_SIZE, obstacle.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
        }
    }

    // Phương thức vẽ bảng thông tin
    private void drawInfoPanel(Graphics2D g2d) {
        // Tạo gradient cho nền bảng thông tin
        GradientPaint gradient = new GradientPaint(
            TILE_SIZE * GRID_WIDTH, 0, new Color(20, 20, 20, 230), 
            TILE_SIZE * GRID_WIDTH + INFO_PANEL_WIDTH, 0, new Color(50, 50, 50, 230)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(TILE_SIZE * GRID_WIDTH, 0, INFO_PANEL_WIDTH, TILE_SIZE * GRID_HEIGHT);

        // Vẽ viền bảng thông tin
        g2d.setColor(new Color(255, 223, 0)); // Màu vàng kim
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(TILE_SIZE * GRID_WIDTH + 5, 5, INFO_PANEL_WIDTH - 10, TILE_SIZE * GRID_HEIGHT - 10);

        // Thiết lập font chữ
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // Vẽ tiêu đề bảng thông tin
        String title = "GAME INFO";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = TILE_SIZE * GRID_WIDTH + (INFO_PANEL_WIDTH - titleWidth) / 2;
        g2d.drawString(title, titleX, 35);
        
        // Vẽ các thông tin game
        String[] labels = {"Score", "Level", "High Score", "Obstacles", "Speed"};
        Object[] values = {score, level, highScore, obstacles.size(), (200 - currentSpeed)};
        
        // Vẽ từng dòng thông tin
        for (int i = 0; i < labels.length; i++) {
            int y = 80 + i * 50;
            
            // Vẽ nền cho từng dòng
            g2d.setColor(new Color(0, 0, 0, 100));
            int infoX = TILE_SIZE * GRID_WIDTH + 20;
            g2d.fillRect(infoX - 5, y - 20, INFO_PANEL_WIDTH - 30, 35);
            
            // Vẽ text với hiệu ứng bóng đổ
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2d.setColor(new Color(0, 0, 0, 120));
            String fullText = labels[i] + ": " + String.valueOf(values[i]);
            int textWidth = g2d.getFontMetrics().stringWidth(fullText);
            int textX = TILE_SIZE * GRID_WIDTH + (INFO_PANEL_WIDTH - textWidth) / 2;
            g2d.drawString(fullText, textX + 2, y + 2);
            
            // Vẽ text chính
            g2d.setColor(new Color(255, 223, 0));
            g2d.drawString(fullText, textX, y);
        }

        // Tạo các nút điều chỉnh tốc độ
        int buttonY = TILE_SIZE * GRID_HEIGHT - 100;
        int buttonX = TILE_SIZE * GRID_WIDTH + (INFO_PANEL_WIDTH - 140) / 2;
        
        // Nút tăng tốc độ
        speedUpButton = new JButton("▲ Speed Up");
        speedUpButton.setBounds(buttonX, buttonY, 140, 30);
        speedUpButton.setBackground(new Color(50, 205, 50));
        speedUpButton.setForeground(Color.WHITE);
        speedUpButton.setFocusPainted(false);
        speedUpButton.addActionListener(e -> adjustSpeed(-10));
        add(speedUpButton);

        // Nút giảm tốc độ
        speedDownButton = new JButton("▼ Speed Down");
        speedDownButton.setBounds(buttonX, buttonY + 35, 140, 30);
        speedDownButton.setBackground(new Color(220, 20, 60));
        speedDownButton.setForeground(Color.WHITE);
        speedDownButton.setFocusPainted(false);
        speedDownButton.addActionListener(e -> adjustSpeed(10));
        add(speedDownButton);
    }

    // Phương thức vẽ màn hình kết thúc game
    private void drawGameOver(Graphics2D g2d) {
        // Tạo lớp phủ mờ
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, TILE_SIZE * GRID_WIDTH, getHeight());

        // Vẽ thông báo "Game Over"
        g2d.setFont(gameOverFont);
        g2d.setColor(Color.RED);
        String message = "Game Over!";
        g2d.drawString(message, (TILE_SIZE * GRID_WIDTH) / 2 - g2d.getFontMetrics().stringWidth(message) / 2, getHeight() / 2);

        // Vẽ hướng dẫn khởi động lại
        g2d.setFont(infoFont);
        String restartMessage = "Press R to Restart";
        g2d.drawString(restartMessage, (TILE_SIZE * GRID_WIDTH) / 2 - g2d.getFontMetrics().stringWidth(restartMessage) / 2, getHeight() / 2 + 30);
    }

    // Xử lý sự kiện timer
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning && !isPaused) moveSnake();
        repaint();
    }

    // Phương thức di chuyển rắn
    private void moveSnake() {
        Point head = snake.getFirst();
        Point newHead;

        // Xác định vị trí mới của đầu rắn dựa trên hướng di chuyển
        switch (direction) {
            case "UP":
                newHead = new Point(head.x, head.y - 1);
                break;
            case "DOWN":
                newHead = new Point(head.x, head.y + 1);
                break;
            case "LEFT":
                newHead = new Point(head.x - 1, head.y);
                break;
            case "RIGHT":
                newHead = new Point(head.x + 1, head.y);
                break;
            default:
                throw new IllegalStateException("Unexpected direction: " + direction);
        }

        // Kiểm tra va chạm
        if (newHead.x < 0 || newHead.y < 0 || newHead.x >= GRID_WIDTH || newHead.y >= GRID_HEIGHT || 
            snake.contains(newHead) || obstacles.contains(newHead)) {
            gameRunning = false;
            return;
        }

        // Thêm đầu mới vào rắn
        snake.addFirst(newHead);
        
        // Xử lý khi ăn được thức ăn
        if (newHead.equals(food)) {
            score += 10;
            if (score > highScore) highScore = score;
            spawnFood();
            // Tăng cấp độ sau mỗi 50 điểm
            if (score % 50 == 0) {
                level++;
                currentSpeed = Math.max(50, currentSpeed - SPEED_INCREMENT);
                timer.setDelay(currentSpeed);
                spawnObstacles();
            }
        } else {
            snake.removeLast(); // Xóa đuôi nếu không ăn được thức ăn
        }
    }

    // Xử lý sự kiện phím
    @Override
    public void keyPressed(KeyEvent e) {
        // Khởi động lại game khi nhấn R
        if (!gameRunning && e.getKeyCode() == KeyEvent.VK_R) {
            initGame();
            return;
        }

        // Tạm dừng/tiếp tục game khi nhấn P
        if (e.getKeyCode() == KeyEvent.VK_P) {
            isPaused = !isPaused;
            return;
        }

        // Xử lý các phím điều hướng
        String newDirection;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                newDirection = "UP";
                break;
            case KeyEvent.VK_DOWN:
                newDirection = "DOWN";
                break;
            case KeyEvent.VK_LEFT:
                newDirection = "LEFT";
                break;
            case KeyEvent.VK_RIGHT:
                newDirection = "RIGHT";
                break;
            default:
                newDirection = direction;
        }

        // Chỉ thay đổi hướng nếu không phải hướng ngược lại
        if (!newDirection.equals(oppositeDirection(direction))) {
            direction = newDirection;
        }
    }

    // Phương thức lấy hướng ngược lại
    private String oppositeDirection(String dir) {
        switch (dir) {
            case "UP":
                return "DOWN";
            case "DOWN":
                return "UP";
            case "LEFT":
                return "RIGHT";
            case "RIGHT":
                return "LEFT";
            default:
                return "";
        }
    }

    // Các phương thức bắt buộc của KeyListener
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Phương thức tải điểm cao từ file
    private void loadHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            highScore = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "High score file not found, starting fresh.");
        }
    }

    // Phương thức lưu điểm cao vào file
    private void saveHighScore() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("highscore.txt"))) {
            bw.write(String.valueOf(highScore));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not save high score.", e);
        }
    }

    // Phương thức main để khởi chạy game
    public static void main(String[] args) {
        // Tạo cửa sổ game
        JFrame frame = new JFrame("Enhanced Snake Game");
        EnhancedSnakeGame gamePanel = new EnhancedSnakeGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Thêm hook để lưu điểm cao khi thoát game
        Runtime.getRuntime().addShutdownHook(new Thread(gamePanel::saveHighScore));
    }
}
