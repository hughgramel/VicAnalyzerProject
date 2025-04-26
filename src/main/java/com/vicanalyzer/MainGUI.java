package com.vicanalyzer;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class MainGUI extends JFrame {
    private final SaveAnalyzer analyzer;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JButton analyzeButton;
    private File selectedFile;
    private JTextField acceptedFileField;
    private JTextField totalFileField;
    private Set<String> selectedTags = new HashSet<>();

    public MainGUI() {
        analyzer = new SaveAnalyzer();
        setupUI();
    }

    private void setupUI() {
        setTitle("Victoria 2 Population Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Welcome label with modern styling
        JLabel welcomeLabel = new JLabel("Victoria 2 Save Game Analyzer");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(51, 51, 51));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Analyze population data from save files");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // File selection button with modern styling
        JButton selectButton = new JButton("Select Save File/Folder");
        selectButton.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        selectButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.setMaximumSize(new Dimension(250, 40));
        selectButton.addActionListener(e -> selectFile());
        mainPanel.add(selectButton);
        mainPanel.add(Box.createVerticalStrut(20));

        // Status label with modern styling
        statusLabel = new JLabel("Please select a Victoria 2 save file (.v2) or folder");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(102, 102, 102));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // File name panel with modern styling
        JPanel fileNamePanel = new JPanel(new GridLayout(2, 2, 15, 15));
        fileNamePanel.setMaximumSize(new Dimension(450, 80));
        fileNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileNamePanel.setBackground(new Color(245, 245, 245));
        fileNamePanel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel acceptedLabel = new JLabel("Accepted pops file name:");
        acceptedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        acceptedFileField = new JTextField("accepted_pops");
        acceptedFileField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        JLabel totalLabel = new JLabel("Total pops file name:");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        totalFileField = new JTextField("total_pops");
        totalFileField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

        fileNamePanel.add(acceptedLabel);
        fileNamePanel.add(acceptedFileField);
        fileNamePanel.add(totalLabel);
        fileNamePanel.add(totalFileField);

        mainPanel.add(fileNamePanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Progress bar with modern styling
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setMaximumSize(new Dimension(450, 25));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        mainPanel.add(progressBar);
        mainPanel.add(Box.createVerticalStrut(20));

        // Analyze button with modern styling
        analyzeButton = new JButton("Analyze Files");
        analyzeButton.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #2196F3; foreground: #ffffff");
        analyzeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        analyzeButton.setEnabled(false);
        analyzeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        analyzeButton.setMaximumSize(new Dimension(250, 40));
        analyzeButton.addActionListener(e -> startAnalysis());
        mainPanel.add(analyzeButton);

        add(mainPanel, BorderLayout.CENTER);
        
        // Set window properties
        setSize(600, 500);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
    }

    private boolean validateV2Files(File file) {
        if (file.isFile()) {
            return file.getName().toLowerCase().endsWith(".v2");
        } else if (file.isDirectory()) {
            File[] files = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".v2"));
            return files != null && files.length > 0 && 
                   Arrays.stream(file.listFiles())
                         .filter(f -> !f.isDirectory())
                         .allMatch(f -> f.getName().toLowerCase().endsWith(".v2"));
        }
        return false;
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle("Select Victoria 2 Save File or Folder");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".v2");
            }
            public String getDescription() {
                return "Victoria 2 Save Files (*.v2)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            if (validateV2Files(selected)) {
                selectedFile = selected;
                statusLabel.setText("Selected: " + selectedFile.getName());
                analyzeButton.setEnabled(true);
                statusLabel.setForeground(Color.BLACK);
            } else {
                selectedFile = null;
                analyzeButton.setEnabled(false);
                statusLabel.setText("Error: Please select a valid Victoria 2 save file (.v2) or a folder containing only .v2 files");
                statusLabel.setForeground(Color.RED);
                JOptionPane.showMessageDialog(this,
                    "Please select a valid Victoria 2 save file (.v2) or a folder containing only .v2 files",
                    "Invalid Selection",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startAnalysis() {
        // Validate file names
        String acceptedName = acceptedFileField.getText().trim();
        String totalName = totalFileField.getText().trim();
        
        if (acceptedName.isEmpty() || totalName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter names for both output files",
                "Invalid File Names",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create custom button panel for options
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 5));

        JButton humanButton = new JButton("Human Countries Only");
        humanButton.setBackground(new Color(100, 180, 100));
        humanButton.setForeground(Color.WHITE);
        humanButton.setOpaque(true);
        humanButton.setBorderPainted(false);
        
        JButton allButton = new JButton("All Countries");
        JButton specificButton = new JButton("Specific Countries");

        buttonPanel.add(humanButton);
        buttonPanel.add(allButton);
        buttonPanel.add(specificButton);

        // Create custom dialog
        JDialog dialog = new JDialog(this, "Analysis Options", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JLabel("  Choose analysis type:", SwingConstants.CENTER), BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        final int[] choice = {-1};

        // Add action listeners
        humanButton.addActionListener(e -> {
            choice[0] = 2; // Index for human countries
            dialog.dispose();
        });
        allButton.addActionListener(e -> {
            choice[0] = 0; // Index for all countries
            dialog.dispose();
        });
        specificButton.addActionListener(e -> {
            dialog.dispose();
            if (showCountryTagSelector()) {
                choice[0] = 1; // Index for specific countries
            }
        });

        // Show dialog
        dialog.setVisible(true);

        if (choice[0] == -1) return;

        // Show progress bar
        progressBar.setVisible(true);
        analyzeButton.setEnabled(false);
        statusLabel.setForeground(Color.BLACK);

        // Run analysis in background thread
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Set the files in analyzer
                    if (selectedFile.isDirectory()) {
                        File[] v2Files = selectedFile.listFiles((dir, name) -> name.toLowerCase().endsWith(".v2"));
                        analyzer.setFiles(v2Files);
                    } else {
                        analyzer.setFiles(new File[]{selectedFile});
                    }

                    // Read files with progress updates
                    analyzer.readFilesWithProgress((progress) -> {
                        publish(progress);
                    });

                    // Set analysis type based on user choice
                    if (choice[0] == 1) { // Specific countries
                        analyzer.setSpecificTags(selectedTags);
                    } else {
                        analyzer.setAnalysisType(choice[0]);
                    }

                    // Use the user-specified file names
                    String acceptedFileName = acceptedName + ".csv";
                    String totalFileName = totalName + ".csv";
                    
                    analyzer.printFiles(acceptedFileName, totalFileName);
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException("Analysis failed: " + e.getMessage(), e);
                }
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                statusLabel.setText("Processing: " + latest + "% complete");
            }

            @Override
            protected void done() {
                try {
                    get(); // This will throw an exception if one occurred during execution
                    progressBar.setVisible(false);
                    analyzeButton.setEnabled(true);
                    statusLabel.setText("Analysis complete! CSV files have been created.");
                    statusLabel.setForeground(Color.BLACK);
                    JOptionPane.showMessageDialog(MainGUI.this,
                        "Analysis complete!\nFiles created:\n- " + acceptedFileField.getText() + ".csv\n- " + totalFileField.getText() + ".csv",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    progressBar.setVisible(false);
                    analyzeButton.setEnabled(true);
                    statusLabel.setText("Error: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(MainGUI.this,
                        "An error occurred during analysis:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private boolean showCountryTagSelector() {
        JDialog tagDialog = new JDialog(this, "Select Country Tags", true);
        tagDialog.setLayout(new BorderLayout(10, 10));
        styleDialog(tagDialog);
        tagDialog.setSize(500, 600);
        tagDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        // Styled explanation text
        JTextArea explanation = new JTextArea(
            "Enter country tags separated by spaces (e.g., ENG FRA RUS).\n" +
            "Tags are usually three letters and MUST be in UPPERCASE.\n\n" +
            "Common tags:\n" +
            "ENG - England          FRA - France\n" +
            "RUS - Russia           PRU - Prussia\n" +
            "USA - United States    AUS - Austria\n" +
            "TUR - Turkey          JAP - Japan"
        );
        explanation.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        explanation.setEditable(false);
        explanation.setBackground(new Color(245, 245, 245));
        explanation.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        explanation.setWrapStyleWord(true);
        explanation.setLineWrap(true);
        mainPanel.add(explanation, BorderLayout.NORTH);

        // Styled input area
        JTextArea tagInput = new JTextArea(5, 30);
        tagInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagInput.setLineWrap(true);
        tagInput.setWrapStyleWord(true);
        tagInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(tagInput);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelButton = createStyledButton("Cancel", new Color(245, 245, 245), Color.BLACK);
        JButton okButton = createStyledButton("OK", new Color(33, 150, 243), Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        tagDialog.add(mainPanel);

        // Track dialog result
        final boolean[] result = {false};

        // Add button actions
        okButton.addActionListener(e -> {
            String tags = tagInput.getText().trim().toUpperCase();
            if (tags.isEmpty()) {
                JOptionPane.showMessageDialog(tagDialog,
                    "Please enter at least one country tag.",
                    "No Tags Entered",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Split and validate tags
            String[] tagArray = tags.split("\\s+");
            if (tagArray.length == 0) {
                JOptionPane.showMessageDialog(tagDialog,
                    "Please enter at least one country tag.",
                    "No Tags Entered",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate tag format
            for (String tag : tagArray) {
                if (!tag.matches("[A-Z]{3}")) {
                    JOptionPane.showMessageDialog(tagDialog,
                        "Invalid tag format: " + tag + "\nTags must be exactly 3 uppercase letters.",
                        "Invalid Tag",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            selectedTags = new HashSet<>(Arrays.asList(tagArray));
            result[0] = true;
            tagDialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            result[0] = false;
            tagDialog.dispose();
        });

        // Show dialog
        tagDialog.setVisible(true);
        return result[0];
    }

    private void styleDialog(JDialog dialog) {
        dialog.getRootPane().putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        ((JComponent) dialog.getContentPane()).setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.putClientProperty(FlatClientProperties.STYLE, 
            String.format("arc: 8; background: #%02x%02x%02x; foreground: #%02x%02x%02x",
                background.getRed(), background.getGreen(), background.getBlue(),
                foreground.getRed(), foreground.getGreen(), foreground.getBlue()));
        return button;
    }

    public static void main(String[] args) {
        // Install FlatLaf Look & Feel
        FlatLightLaf.setup();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
} 