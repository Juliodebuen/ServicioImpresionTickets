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
    
	@Scheduled(fixedRate = 3000)
	public void scheduled() {
		System.out.println("Comienza busqueda de elementos en cola");
		if(!colaImpresion.isEmpty()) {
			boolean isFoundNothing = false;
			int currentIndex = 0;
			int beforeSimbolIndex = 0;
			String contenido = colaImpresion.get(0).getMessage(); //mucho contenido rA por aqui rA
			byte ESCSquence[] = new byte[]{0x1B, 0x7C};
			try {
				if(!contenido.contains(new String(ESCSquence))) {
					while(!isFoundNothing) {
						
						//System.out.println(new String(ESCSquence).length());
						
						//						0->18 				30					
						if(contenido.substring(currentIndex, contenido.length()).contains("bC")){
						//		16->28					0->18							0->18			30				16->10
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("bC"); // mucho contenido -> por aqui
						//												18				28								
							String primerMitad = contenido.substring(currentIndex, beforeSimbolIndex) + new String(ESCSquence); // mucho contenido [ESCSquence] -> por aqui [ESCSquence]
						//												28					30			
							String segundaMitad = contenido.substring(beforeSimbolIndex, contenido.length()); //rA por aqui rA -> rA
						//											0->18	
							contenido = contenido.substring(0, currentIndex) + primerMitad + segundaMitad; //mucho contenido [ESCSquence]rA por aqui rA -> por aqui rA
						//		18			16
							currentIndex = beforeSimbolIndex +4; 
						}else {
							isFoundNothing = true; 
							colaImpresion.get(0).setMessage(contenido);
							System.out.println(contenido);
						}
						
						
					/*	if(colaImpresion.get(0).getMessage().contains("rA")) {
							String str = colaImpresion.get(0).getMessage();
							String aux = str.substring(currentIndex, str.indexOf("rA")) + new String(ESCSquence);
							aux = aux + str.substring(str.indexOf("rA") , str.length());
							colaImpresion.get(0).setMessage(aux);
							System.out.println(aux);
						}
						else if(colaImpresion.get(0).getMessage().contains("cA")) {
							String str = colaImpresion.get(0).getMessage();
							String aux = str.substring(0, str.indexOf("cA")) + new String(ESCSquence);
							aux = aux + str.substring(str.indexOf("cA") , str.length());
							colaImpresion.get(0).setMessage(aux);
						}
						else if(colaImpresion.get(0).getMessage().contains("bC")) {
							System.out.println("entro aqui");
							String str = colaImpresion.get(0).getMessage();
							String aux = str.substring(0, str.indexOf("bC")) + new String(ESCSquence);
							aux = aux + str.substring(str.indexOf("bC") , str.length());
							colaImpresion.get(0).setMessage(aux);
							
							System.out.println(aux);
						}
						else if(colaImpresion.get(0).getMessage().contains("1uC")) {
							String str = colaImpresion.get(0).getMessage();
							String aux = str.substring(0, str.indexOf("1uC")) + new String(ESCSquence);
							aux = aux + str.substring(str.indexOf("1uC") , str.length());
							colaImpresion.get(0).setMessage(aux);
						}
						else if(colaImpresion.get(0).getMessage().contains("4C")) {
							String str = colaImpresion.get(0).getMessage();
							String aux = str.substring(0, str.indexOf("4C")) + new String(ESCSquence);
							aux = aux + str.substring(str.indexOf("4C") , str.length());
							colaImpresion.get(0).setMessage(aux);
						}
						else if(colaImpresion.get(0).getMessage().contains("3C")) {
							String str = colaImpresion.get(0).getMessage();
							String aux = str.substring(0, str.indexOf("3C")) + new String(ESCSquence);
							aux = aux + str.substring(str.indexOf("3C") , str.length());
							colaImpresion.get(0).setMessage(aux);
						}
						else if(colaImpresion.get(0).getMessage().contains("2C")) {
							String str = colaImpresion.get(0).getMessage();
							String aux = str.substring(0, str.indexOf("2C")) + new String(ESCSquence);
							aux = aux + str.substring(str.indexOf("2C") , str.length());
							colaImpresion.get(0).setMessage(aux);
						}else {
							isFoundNothing = true;
							System.out.println("get in");
						}*/
					}
				}
				
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
				e.printStackTrace();
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
