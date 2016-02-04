
package arduinoserial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import javax.swing.Timer;

/**
 * Comport communication
 * @author Judge
 */
public class ComPort {
    
    public String portName;
    private InputStream in;
    private Timer timer;
        
    public ComPort(String portName){
        this.portName = portName;
    }
    
    public void connect() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException{
                
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);  
   
        if (portIdentifier.isCurrentlyOwned()) {  
            throw new PortInUseException();
        } else {  
            // points who owns the port and connection timeout  
            SerialPort serialPort = (SerialPort) portIdentifier.open("RS232Example", 2000);  
              
            // setup connection parameters  
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
   
            // setup serial port writer  
            CommPortSender.setWriterStream(serialPort.getOutputStream());  
          
            // save input stream
            this.in = serialPort.getInputStream();
        }
    }
    
    public void write(String data){
        CommPortSender.send(new ProtocolImpl().getMessage(data));  
    }
    
    public void read(ArduinoSerialUI window){
        this.timer = new Timer(1000, new ActionListener() {
            
            private StringBuffer s = new StringBuffer("");
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                try {
                    int b;
                    
                    // if stream is not bound in.read() method returns -1  
                    while ((b = in.read()) != -1) {
                        if ((char)b!='\n') {
                            s.append((char)b);
                        } else {
                            window.result.setText(s.toString());
                            System.out.println(s.toString()); 
                            s = new StringBuffer("");
                        }
                    }
                    // wait 10ms when stream is broken and check again  
                    sleep(10);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        });
        this.timer.start();
        
    }
    
}
