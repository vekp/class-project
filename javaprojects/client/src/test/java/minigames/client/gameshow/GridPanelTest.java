package minigames.client.gameshow;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class GridPanelTest {

    @Mock
    private Graphics graphicsMock;

    @Mock
    private Graphics2D graphics2DMock;

    private GridPanel gridPanel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ImageIcon imageIcon = new ImageIcon(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
        gridPanel = new GridPanel(imageIcon);
    }

    @Test
    public void testSetFadeCell_ValidCoordinates() {
        gridPanel.setFadeCell(0, 0);

        // Verify that the repaint method was called
        verify(gridPanel).repaint();

        // Verify that the faded cell was marked
        assertTrue(gridPanel.isCellVisible(0, 0));
    }

    @Test
    public void testSetFadeCell_InvalidCoordinates() {
        // Attempt to fade a cell with invalid coordinates
        gridPanel.setFadeCell(-1, 0);
        gridPanel.setFadeCell(0, -1);
        gridPanel.setFadeCell(10, 0);
        gridPanel.setFadeCell(0, 10);

        // Verify that the repaint method was never called
        verify(gridPanel, never()).repaint();

        // Verify that the faded cells remain unchanged
        assertTrue(gridPanel.isCellVisible(0, 0));
        assertTrue(gridPanel.isCellVisible(0, 1));
        assertTrue(gridPanel.isCellVisible(1, 0));
    }

    @Test
    public void testPaintComponent() {
        // Mock the behavior of Graphics and Graphics2D
        when(graphics2DMock.create()).thenReturn(graphics2DMock);
        when(graphicsMock.create()).thenReturn(graphics2DMock);

        Graphics2D graphics2D = (Graphics2D) graphicsMock;

        // Set up some faded cells
        gridPanel.setFadeCell(0, 0);
        gridPanel.setFadeCell(1, 1);

        // Invoke paintComponent and check if the expected methods were called
        gridPanel.paintComponent(graphicsMock);

        // Verify that the image is drawn
        verify(graphics2D).drawImage(any(Image.class), eq(0), eq(0), eq(gridPanel.getWidth()), eq(gridPanel.getHeight()), any());

        // Verify that the faded cells are drawn with reduced opacity
        verify(graphics2D, times(2)).setComposite(any(AlphaComposite.class));
        verify(graphics2D, times(2)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
    }
}
