package com.example.demo.schedule;

import java.util.ArrayList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.demo.objects.ImpresionInfo;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.util.JposPropertiesConst;

@Component
public class Schedule {    
    public static ArrayList<ImpresionInfo> colaImpresion = new ArrayList<>();
    private POSPrinter ptr;
    
    private int findClosestElement(String contenido) {
		//int[] simbols = new int[10];
		ArrayList<Integer> simbols = new ArrayList<>();
		ArrayList<Integer> finalSimbols = new ArrayList<>();
		simbols.add(contenido.indexOf("bC"));	
		simbols.add(contenido.indexOf("cA"));	
		simbols.add(contenido.indexOf("rA"));	
		simbols.add(contenido.indexOf("5uC"));	
		simbols.add(contenido.indexOf("2C"));	
		simbols.add(contenido.indexOf("3C"));	
		simbols.add(contenido.indexOf("4C"));	
		simbols.add(contenido.indexOf("3hC"));	
		simbols.add(contenido.indexOf("3vC"));	
		simbols.add(contenido.indexOf("1uC"));
		if(contenido.contains("N")) {
			//System.out.println("got in " + (contenido.charAt(contenido.indexOf("N")+1) == ' '));
			if(contenido.charAt(contenido.indexOf("N") + 1) == ' ') {
				simbols.add(contenido.indexOf("N"));	
			}
		}
		for(int  i = 0; i < simbols.size(); i++) {
			if(simbols.get(i) > -1) {
				finalSimbols.add(simbols.get(i));
			}
		}
		
		int minAt = 0;
		for (int i = 1; i < finalSimbols.size(); i++) {
			minAt = finalSimbols.get(i) < finalSimbols.get(minAt) ? i : minAt;
		}
		if(finalSimbols.size() > 0) {
			System.out.println("CLOSEST ELEMENT: "+ finalSimbols.get(minAt));
			return finalSimbols.get(minAt);
		}else {
			return -1;
		}
    }
    
	@Scheduled(fixedRate = 3000)
	public void scheduled() {
		System.out.println("Comienza busqueda de elementos en cola");
		if(!colaImpresion.isEmpty()) {
			boolean isFoundNothing = false;
			int currentIndex = 0;
			int beforeSimbolIndex = 0;
			String contenido = colaImpresion.get(0).getMessage(); //mucho contenido rA por aqui rA
			byte ESCSquence[] = new byte[]{0x1B, 0x7C};
			if(!contenido.contains(new String(ESCSquence))) {
				while(!isFoundNothing) {
					int closestElement = findClosestElement(contenido.substring(currentIndex, contenido.length()));
					if(closestElement > -1) {
						beforeSimbolIndex = currentIndex + closestElement;
					}else {
						isFoundNothing = true;
						//colores
						colaImpresion.get(0).setMessage(contenido);
						System.out.println(contenido);
						return;
					}
					String primerMitad = contenido.substring(currentIndex, beforeSimbolIndex) + new String(ESCSquence);
					String segundaMitad = contenido.substring(beforeSimbolIndex, contenido.length()); 
					contenido = contenido.substring(0, currentIndex) + primerMitad + segundaMitad;
					currentIndex = beforeSimbolIndex +4; 
				}
			}
			try {	
				ptr = new POSPrinter();
				System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "src/main/java/jpos.xml");
				String logicalName = "SRP-350plusIII";
				ptr.open(logicalName);
				ptr.claim(1000);
				ptr.setDeviceEnabled(true);
				
				String imp = colaImpresion.get(0).getMessage();
				System.out.println(imp);
				ptr.printNormal(POSPrinterConst.PTR_S_RECEIPT, imp);
				
				//ptr.printNormal(POSPrinterConst.PTR_S_RECEIPT, new String(ESCSquence) + "7lF");
				//ptr.cutPaper(10);
				System.out.println("ticket "+ colaImpresion.get(0).getCount()+" impreso");
				colaImpresion.remove(0);
			}catch(JposException e) {
				System.out.println(e.getMessage());
			}finally {
				try {
					ptr.setDeviceEnabled(false);
					ptr.release();
					ptr.close();
				}catch(JposException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
