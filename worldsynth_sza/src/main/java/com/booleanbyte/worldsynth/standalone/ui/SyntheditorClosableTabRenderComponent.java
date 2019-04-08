
package com.booleanbyte.worldsynth.standalone.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.standalone.ui.syntheditor.SynthEditorPanel;

public class SyntheditorClosableTabRenderComponent extends JPanel {
	private static final long serialVersionUID = -3176707757255847273L;
	
	private Synth synth;
	private WorldSynthMainUI mainUi;
	
	public SyntheditorClosableTabRenderComponent(final SynthEditorPanel synthEditor, final WorldSynthMainUI mainUi) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(false);
		
		this.synth = synthEditor.getSynth();
		this.mainUi = mainUi;
		
		JLabel label = new JLabel() {
			private static final long serialVersionUID = -6199735949709099499L;

			public String getText() {
				if(synthEditor.hasUnsavedChanges()) {
					return "*" + synth.getName();
				}
                return synth.getName();
            }
        };
        
        add(label);
//		add(new JLabel(synth.getName()));
		JButton button = new TabButton();
		add(button);
	}

	private class TabButton extends JButton implements ActionListener {
		private static final long serialVersionUID = -8608315775145034586L;

		public TabButton() {
			int size = 12;
			setPreferredSize(new Dimension(size, size));
			setToolTipText("close this editor");
			// Make the button looks the same for all Laf's
			setUI(new BasicButtonUI());
			// Make it transparent
			setContentAreaFilled(false);
			// No need to be focusable
			setFocusable(false);
			setBorderPainted(false);
			// Making nice rollover effect
			setRolloverEnabled(true);
			// Close the proper tab by clicking the button
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			mainUi.closeSynthEditor(synth);
		}

		// paint the cross
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			// shift the image for pressed buttons
			if (getModel().isPressed()) {
				g2.translate(1, 1);
			}
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);
			if (getModel().isRollover()) {
				g2.setColor(Color.MAGENTA);
			}
			int delta = 2;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}
	}
}
