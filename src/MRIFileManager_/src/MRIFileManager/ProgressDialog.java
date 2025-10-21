package MRIFileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ProgressDialog extends JDialog {

    private static final long serialVersionUID = 1L;
	private final JProgressBar progressBar;
    private final JLabel messageLabel;
    private final JButton cancelButton;
    private SwingWorker<?, ?> worker;

    public ProgressDialog(Frame parent, String message, boolean indeterminate, boolean showCancelButton) {
        super(parent, "Processing in progress...", true);

        messageLabel = new JLabel(message);
        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(indeterminate);
        progressBar.setStringPainted(!indeterminate);

        cancelButton = new JButton("Annuler");
        cancelButton.setVisible(showCancelButton);
        cancelButton.addActionListener((ActionEvent e) -> {
            if (worker != null) {
                worker.cancel(true);
                cancelButton.setEnabled(false);
                messageLabel.setText("Annulation en cours...");
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(messageLabel, BorderLayout.NORTH);
        centerPanel.add(progressBar, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout(10, 10));
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(350, 120);
        setLocationRelativeTo(parent);
    }

    public void setProgress(int value) {
        if (!progressBar.isIndeterminate()) {
            progressBar.setValue(value);
        }
    }

    public void close() {
        dispose();
    }

    public void bindToWorker(SwingWorker<?, ?> worker) {
        this.worker = worker;
        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    int progress = (Integer) evt.getNewValue();
                    setProgress(progress);
                }
            }
        });
    }
}
