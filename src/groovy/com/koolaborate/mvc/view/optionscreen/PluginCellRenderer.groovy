package com.koolaborate.mvc.view.optionscreen

import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.Paint
import java.awt.RenderingHints
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.image.BufferedImage
import java.util.EventObject
import javax.swing.AbstractCellEditor
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.GrayFilter
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.JTextArea
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer
import plug.engine.PlugEngine
import plug.engine.PlugEngineException
import plug.engine.Pluggable
import plug.engine.ui.swing.Messages
import plug.engine.ui.swing.PluggableWrapper

/**
 * @author Christophe Le Besnerais
 */
class PluginCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
	
	private PluggableWrapper plugin
	
	
	public Component getTableCellRendererComponent(final JTable table, Object value, final boolean isSelected, boolean hasFocus, final int row, int column) {
		if (value == null)
			return new JLabel()
		
		final Pluggable plugin = ((PluggableWrapper) value).getPluggable()
		final int height = isSelected ? 150 : 55 

		JPanel panel = [		
			paintComponent: { graphics ->
				Graphics g = graphics
				super.paintComponent(g)
				Graphics2D g2d = (Graphics2D) g	
				
				Paint paint = Color.WHITE
				if (isSelected){
					paint = new GradientPaint(0, 0, new Color(216, 225, 233), 0, height, new Color(179, 196, 213))
				}
					
				g2d.setPaint(paint)
				g2d.fillRect(0, 0, table.getWidth(), height - 1)
				g2d.setColor(new Color(127, 157, 185))
				float[] dash = [1.0f]
				g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f))
				g2d.drawLine(0, height - 1, table.getWidth(), height - 1)
				g2d.setStroke(new BasicStroke())
			}
		] as JPanel
	
		panel.setBorder(new EmptyBorder(5, 5, 5, 5))
		panel.setLayout(new BorderLayout(5, 0))
		
		final JLabel icon = new JLabel()
		icon.setVerticalAlignment(SwingConstants.TOP)
		icon.setPreferredSize(new Dimension(50, height))
		if (plugin.getIcon() != null) {
			ImageIcon img = new ImageIcon(getThumbnail(plugin.icon, 40))
			icon.setIcon(img)			
		}
		panel.add(icon, BorderLayout.WEST)
		
		JPanel rightPanel = new JPanel(new BorderLayout(3, 3), true)
		rightPanel.setOpaque(false)
		panel.add(rightPanel, BorderLayout.CENTER)
		
		JPanel upPanel = new JPanel(new BorderLayout(), true)
		upPanel.setOpaque(false)
		final JLabel name = new JLabel(plugin.getName())
		name.setFont(name.getFont().deriveFont(Font.BOLD))
		final JLabel version = new JLabel(plugin.getVersion() == null ? "" : plugin.getVersion()) //$NON-NLS-1$
		version.setFont(name.getFont().deriveFont(Font.PLAIN))
		version.setForeground(Color.GRAY)
		upPanel.add(name, BorderLayout.WEST)
		upPanel.add(version, BorderLayout.EAST)
		rightPanel.add(upPanel, BorderLayout.NORTH)
		
		String pluginDescription = plugin.getDescription() == null ? "" : plugin.getDescription()
		final JTextArea description = new JTextArea(pluginDescription) //$NON-NLS-1$
		if (!PlugEngine.getInstance().isCompatible(plugin)){
			description.setText(Messages.getString("PluginCellRenderer.13")) //$NON-NLS-1$
		}
			
		description.setOpaque(false)
		description.setEditable(false)
		description.setLineWrap(true)
		description.setFont(name.getFont().deriveFont(Font.PLAIN))
		rightPanel.add(description, BorderLayout.CENTER)
		
		if (!PlugEngine.getInstance().isEnabled(plugin)) {
			name.setForeground(Color.GRAY)
			version.setForeground(Color.LIGHT_GRAY)
			description.setForeground(Color.GRAY)
			if (plugin.getIcon() != null) {
				ImageIcon img = new ImageIcon(GrayFilter.createDisabledImage(getThumbnail(plugin.getIcon(), 40)))
				icon.icon = img		
			}
		}
		
		if (isSelected) {
			JPanel bottomPanel = new JPanel(true)
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS))
			bottomPanel.setOpaque(false)
			final JButton option = new JButton(Messages.getString("PluginCellRenderer.4")) //$NON-NLS-1$
			option.setOpaque(false)
			if (plugin.hasOptions() && PlugEngine.getInstance().isEnabled(plugin)){
				option.addActionListener([
					actionPerformed: {
						plugin.openOptions()
					}
				] as ActionListener)
			} else{
				option.setEnabled(false)
			}
			
			JButton delete = new JButton(Messages.getString("PluginCellRenderer.5")) //$NON-NLS-1$
			delete.setOpaque(false)
			delete.addActionListener([
				actionPerformed: { e ->
					int confirm = JOptionPane.showConfirmDialog(table, Messages.getString("PluginCellRenderer.6") + plugin.getName() + Messages.getString("PluginCellRenderer.7"), Messages.getString("PluginCellRenderer.8"), JOptionPane.YES_NO_OPTION) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					if (confirm == JOptionPane.YES_OPTION) {
						try {
							PlugEngine.getInstance().removePluggable(plugin)
							DefaultTableModel model = (DefaultTableModel) table.getModel()
							model.removeRow(row)
							table.setModel(new DefaultTableModel())
							table.setModel(model)
							
						} catch (PlugEngineException e1) {
							e1.printStackTrace()
							JOptionPane.showMessageDialog(table, Messages.getString("PluginCellRenderer.9") + plugin.getName(), Messages.getString("PluginCellRenderer.10"), JOptionPane.ERROR_MESSAGE) //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			] as ActionListener)
			
			JCheckBox update = new JCheckBox(Messages.getString("PluginCellRenderer.11"), PlugEngine.getInstance().isUpdateEnabled(plugin)) //$NON-NLS-1$
			update.setOpaque(false)
			update.addItemListener([
				itemStateChanged: { e ->
					try {
						boolean enabled = e.getStateChange() == ItemEvent.SELECTED
						PlugEngine.getInstance().setUpdateEnabled(plugin, enabled)
												
					} catch (PlugEngineException e1) {
						e1.printStackTrace()
					}
				}
			] as ItemListener)
			update.setHorizontalAlignment(SwingConstants.RIGHT)
			
			JCheckBox enabled = new JCheckBox(Messages.getString("PluginCellRenderer.12"), PlugEngine.getInstance().isEnabled(plugin)) //$NON-NLS-1$
			enabled.setOpaque(false)
			enabled.setEnabled(PlugEngine.getInstance().isCompatible(plugin))
			enabled.addItemListener([
				itemStateChanged: { e ->
					try {
						boolean isEnabled = e.getStateChange() == ItemEvent.SELECTED
						PlugEngine.getInstance().setEnabled(plugin, isEnabled)
						option.setEnabled(isEnabled)
						name.setForeground(isEnabled ? Color.BLACK : Color.GRAY)
						version.setForeground(isEnabled ? Color.GRAY : Color.LIGHT_GRAY)
						description.setForeground(isEnabled ? Color.BLACK : Color.GRAY)
						ImageIcon img = null
						if (isEnabled){
							img = new ImageIcon(getThumbnail(plugin.getIcon(), 40))
						} else {
							img = new ImageIcon(GrayFilter.createDisabledImage(getThumbnail(plugin.getIcon(), 40)))
						}
							
						icon.icon = img						
						
					} catch (PlugEngineException e1) {
						e1.printStackTrace()
					}
				}
			] as ItemListener)
			
			bottomPanel.add(option)
			bottomPanel.add(Box.createHorizontalGlue())
			bottomPanel.add(update)
			bottomPanel.add(enabled)
			bottomPanel.add(delete)
			rightPanel.add(bottomPanel, BorderLayout.SOUTH)
		}
		
		if (table.getRowHeight(row) != height)
			table.setRowHeight(row, height)
		panel.setPreferredSize(new Dimension(50, height))
		return panel
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		plugin = (PluggableWrapper) value
		return this.getTableCellRendererComponent(table, value, isSelected, isSelected, row, column)
	}

	public Object getCellEditorValue() {
		return plugin
	}
	
	@Override
	public boolean isCellEditable(EventObject e) {
		return true
	}

	
	protected static BufferedImage getThumbnail(BufferedImage image, int size) {
		float ratio
		int width = image.getWidth()
		int height = image.getHeight()
		if (width > height) {
			ratio = (float) width / (float) height
			width = size
			height = (int) (size / ratio)			
		} else {
			ratio = (float) height / (float) width
			height = size
			width = (int) (size / ratio)
		}
		
		GraphicsConfiguration conf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
		BufferedImage thumbnail = conf.createCompatibleImage(width, height, image.getTransparency())
		Graphics2D g2 = thumbnail.createGraphics()
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		g2.drawImage(image, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), null)
		g2.dispose()
		
		return thumbnail
	}
	
}
