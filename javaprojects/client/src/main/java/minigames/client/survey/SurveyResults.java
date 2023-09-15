package minigames.client.survey;

import minigames.client.MinigameNetworkClient;

import io.vertx.core.json.JsonObject;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import javax.swing.border.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.*;

public class SurveyResults extends JPanel {

    public SurveyResults(MinigameNetworkClient mnClient, String gameId) {
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridLayout(0, 1));
        this.setLayout(new BorderLayout());
        // readBackgroundImage();
    }
}
