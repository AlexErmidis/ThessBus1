import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.zxing.NotFoundException;

public class TicketInspector extends User implements Serializable {

	public String name;
	public String inspector_num;
	//����������� ��������� ����������� 
	public HashMap<String, Integer> durations = new HashMap<>(); 

	
	public TicketInspector(String aName, String aPassword, String username) {
		super(username, aPassword);
		this.name = aName;

		durations.put("14", 60);
		durations.put("02", 70);
		durations.put("31", 65);
		durations.put("30", 70);
		durations.put("78N", 80);
		
	}
	
	public Product browseQR(String filepath_of_qr) throws FileNotFoundException, NotFoundException, IOException {			
		String product_num;
		Product product;
		
		product_num = QRcode.decodeQRCodeImage(filepath_of_qr);
		
		product = (Product) FileManager.search(product_num, "Products.dat");
		
		return product;
	}
	
	//duration = {70, 90, 120, 1, 3, 6, 12} depending on the product (multi-way ticket or card)
	//bus, validation = 0, null if product = card
	//validation = null if product = 1way ticket
	
	//�� ������� �� ������� ����� ���� ���� ���� ��� 12
	public boolean ticketValidation(String date_time, int duration, String bus, String validation_date_time, double price) throws ParseException {
		Set<Integer> foo = new HashSet<>();
		foo.add(1);
		foo.add(3);
		foo.add(6);
		foo.add(12);
		
		String dates = date_time.substring(0,10);
		String times  = date_time.substring(11, 19);
		
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		DateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		Date date = null;
		Date time = null;
			
		date = sdf.parse(dates);
		time = sdf2.parse(times);
		
		//Date current_date = Calendar.getInstance().getTime();
		Date current_date;
		current_date = sdf.parse(sdf.format(new Date()));
		Date current_time;
		current_time = sdf2.parse(sdf2.format(new Date()));
		
		if(foo.contains(duration)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MONTH, 1);
			Date expiration_date = cal.getTime();
			expiration_date = sdf.parse(sdf.format(expiration_date));
			
			//� ����� ��� �����
			if((expiration_date.after(current_date)) || (expiration_date.equals(current_date))) {
				return true;
			}
			else //� ����� ���� �����
				return false;
			
		}
		
		if(date.equals(current_date)) {
		
			//������� ����� ���������� �� ��������� ��� �� ��������� ����� �� 78�
			if((bus.equals("N1") || bus.equals("N1A")) && (price != 2.0))
				return false;
			
			if(validation_date_time.equals("-") == false) {
				String validation_times = validation_date_time.substring(11, 19);
				Date validation_time = null;
				validation_time = sdf2.parse(validation_times);
				long diff = validation_time.getTime() - time.getTime();
			
				if(diff/(1000*60) <= duration) {
					
					long diff2 = current_time.getTime() - validation_time.getTime();
					if(diff2/(1000*60) > durations.get(bus))
						return false;
					return true;
				}
				return false;
			}
			else {
				long diff2 = current_time.getTime() - time.getTime();
				if(diff2/(1000*60) > durations.get(bus))
					return false;
				return true;
			}
		
		}
		else
			return false;
	}

}

//���������� �� ����� �� ������� ��� �������� ��� ������� ���� closeOperation?? �
//�� ������� � ������ ��� ���������� � ������� ��� FileManager ��� �������� ��� �������� 
//�� ����� ��� ��� SignOut �
//�� ���������� ������� ��� retrieve ��������� ��� ��������� ���� ��� ������� ��� �� �������
//����� ���� ��� ���������� ��������� ���������� ��� ������� �
//Alerts ��� login �� ��� ����������� �������� � 
//�������� ������������ ��� ������ ��� ������� ��� ����� ���������, ������ ��������� �
//�� ������������ boolean ��������� � �� ������ ��� �� ���������� ���� �������; - ticketValidation �
//Register -> StartScreen ������ stage �
//�������� ���������� ������ ��� navBar ��� ��� ������ �
//�������� �������� ��������� ��� ��������, ��������� ��������� ��� �������� ������ -> ������
//DepositController!! �
//������� ����������: �� ����������� �������