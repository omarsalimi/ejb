package client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Font;
import javax.swing.JTextField;
import com.toedter.calendar.JDateChooser;
import dao.IDaoRemote;
import entities.Smartphone;
import entities.User;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import java.awt.SystemColor;

public class SmartphoneUI {
	
	private IDaoRemote<User> daoUtilisateur;
	private IDaoRemote<Smartphone> daoSmartphone;
	private JFrame frmMachineUi;
	private JComboBox ComboBox;
	private String[] listUsers;
	private JTextField imeiTxt;
	private JScrollPane scrollPane;
	private JTable table;


	private static IDaoRemote lookUpUser() throws NamingException {
		Hashtable<Object, Object> config = new Hashtable<Object, Object>();
		config.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		config.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		final Context context = new InitialContext(config);
		return (IDaoRemote) context.lookup("ejb:/Géolocalisation-Server/UserService!dao.IDaoRemote");
	}

	private static IDaoRemote lookUpSmartphone() throws NamingException {
		Hashtable<Object, Object> config = new Hashtable<Object, Object>();
		config.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		config.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		final Context context = new InitialContext(config);
		return (IDaoRemote) context.lookup("ejb:/Géolocalisation-Server/SmartphoneService!dao.IDaoRemote");
	}

	private void remplirTab() throws RemoteException {

		List<Smartphone> listSmartphones = daoSmartphone.getAll();
		List<String[]> data = new ArrayList();

		for (Smartphone s : listSmartphones) {
			data.add(new String[] { s.getImei(), s.getUser().getNom() + " " + s.getUser().getPrenom(),
					String.valueOf(s.getId()) });
		}

		List<String> columns = new ArrayList<String>();
		columns.add("IMEI");
		columns.add("Utilisateur");
		columns.add("id");

		TableModel tableModel = new DefaultTableModel(data.toArray(new Object[][] {}), columns.toArray());
		table.setModel(tableModel);
		table.removeColumn(table.getColumnModel().getColumn(2));
	}

	private void vider() {
		imeiTxt.setText("");
		ComboBox.setSelectedIndex(-1);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SmartphoneUI window = new SmartphoneUI();
					window.frmMachineUi.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SmartphoneUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		try {
			daoUtilisateur = lookUpUser();
			daoSmartphone = lookUpSmartphone();
		} catch (NamingException e3) {
			e3.printStackTrace();
		}

		frmMachineUi = new JFrame();
		frmMachineUi.getContentPane().setBackground(SystemColor.activeCaption);
		frmMachineUi.setTitle("Smartphone UI");
		frmMachineUi.setBounds(100, 100, 431, 373);
		frmMachineUi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMachineUi.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Gestion Samrtphone");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 22, 400, 43);
		frmMachineUi.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("IMEI");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setBounds(10, 90, 100, 14);
		frmMachineUi.getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Utilisateur");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_2.setBounds(220, 90, 100, 14);
		frmMachineUi.getContentPane().add(lblNewLabel_2);

		imeiTxt = new JTextField();
		imeiTxt.setBounds(10, 107, 120, 20);
		frmMachineUi.getContentPane().add(imeiTxt);
		imeiTxt.setColumns(10);

		JButton ajouterBtn = new JButton("Ajouter");
		ajouterBtn.setBounds(10, 304, 103, 23);
		frmMachineUi.getContentPane().add(ajouterBtn);

		JButton modifierBtn = new JButton("Modifier");
		modifierBtn.setBounds(138, 304, 103, 23);
		frmMachineUi.getContentPane().add(modifierBtn);

		JButton supprimerBtn = new JButton("Supprimer");
		supprimerBtn.setBounds(302, 304, 103, 23);
		frmMachineUi.getContentPane().add(supprimerBtn);


		List<User> users = daoUtilisateur.getAll();
		listUsers = new String[users.size()];
		for (int i = 0; i < users.size(); i++) {
			listUsers[i] = users.get(i).getNom() + " " + users.get(i).getPrenom();
		}
		ComboBox = new JComboBox(listUsers);
		ComboBox.setBounds(220, 106, 128, 21);
		frmMachineUi.getContentPane().add(ComboBox);
		
		table = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};

			try {
				remplirTab();
			} catch (RemoteException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			scrollPane = new JScrollPane(table);
			scrollPane.setBounds(10, 138, 400, 145);
			frmMachineUi.getContentPane().add(scrollPane);

		ajouterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String imei = imeiTxt.getText();
				int idIndex = ComboBox.getSelectedIndex();

				User u = users.get(idIndex);
				Smartphone smartphone = new Smartphone(imei, u);
				try {
					daoSmartphone.create(smartphone);
					vider();
					remplirTab();

				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});

		modifierBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String imei = imeiTxt.getText();
				int idIndex = ComboBox.getSelectedIndex();

				User u = users.get(idIndex);
				Smartphone smartphone = new Smartphone(imei, u);

				int id = Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 2).toString());
				smartphone.setId(id);
				try {
					daoSmartphone.update(smartphone);
					remplirTab();
					vider();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}

		});

		supprimerBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Smartphone smarphone = new Smartphone();
				int id = Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 2).toString());
				smarphone.setId(id);

				try {
					daoSmartphone.delete(smarphone);
					remplirTab();
					vider();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});

		table.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				int id = Integer.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 2).toString());
				Smartphone smartphone = daoSmartphone.getById(id);
				imeiTxt.setText(smartphone.getImei());
				ComboBox.setSelectedItem(smartphone.getUser().getNom() + " " + smartphone.getUser().getPrenom());

				for (int i = 0; i < listUsers.length; i++) {
					if (smartphone.getUser().getNom() + " " + smartphone.getUser().getPrenom() == listUsers[i]) {
						ComboBox.setSelectedIndex(i);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}
}
