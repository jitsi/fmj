package example5_4;

import java.io.IOException;

import javax.media.DataSink;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.control.StreamWriterControl;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

/**
 * Copies a WAV file using a processor.
 * Adapted From http://java.sun.com/products/java-media/jmf/2.1.1/guide/JMFCapturing.html
 * 
 *
 */
public class Example5_4Modified
{
	public static void main(String[] args)
	{
		String srcUrl = "file:///home/ken/Dev/LTI/fmj/samplemedia/gulp2.wav";
		String destUrl = "file:///home/ken/Desktop/foo.wav";
		String outputType = FileTypeDescriptor.WAVE;
		
//		 CaptureDeviceInfo di = null;
	 	 Processor p = null;
	 	 StateHelper sh = null;
//	 	 Vector deviceList = CaptureDeviceManager.getDeviceList(new
//	 	 	 	 	 AudioFormat(AudioFormat.LINEAR, 44100, 16, 2));
//	 	 if (deviceList.size() > 0)
//	 	     di = (CaptureDeviceInfo)deviceList.firstElement();
//	 	 else
//	 	     // Exit if we can't find a device that does linear, 
//	          // 44100Hz, 16 bit,
//	 	     // stereo audio.
//	 	     System.exit(-1);
	 	 try {
	 	     p = Manager.createProcessor(new MediaLocator(srcUrl));
	 	     System.out.println("Processor: " + p);
	 	     sh = new StateHelper(p);
	 	 } catch (IOException e) {
	 		 e.printStackTrace();
	 	     System.exit(-1);
	 	 } catch (NoProcessorException e) {
	 		 e.printStackTrace();
	 	     System.exit(-1);
	 	 }
	 	 // Configure the processor
	 	 if (!sh.configure(100000))
	 	 {   System.err.println("Failed to configure within 100 secs");
	 		 System.exit(-1);
	 	 }
	 	 // Set the output content type and realize the processor
	 	 p.setContentDescriptor(new
	                  FileTypeDescriptor(outputType));
	 	 if (!sh.realize(100000))
	 	 {   
	 		System.err.println("Failed to realize within 100 secs");
	 		 System.exit(-1);
	 	 }
	 	 // get the output of the processor
	 	 DataSource source = p.getDataOutput();
	 	 System.out.println("p.getDataOutput()=" + source);
	 	System.out.println("source.getContentType()=" + source.getContentType());
	 	 // create a File protocol MediaLocator with the location of the
	 	 // file to which the data is to be written
	 	 MediaLocator dest = new MediaLocator(destUrl);
	 	 // create a datasink to do the file writing & open the sink to
	 	 // make sure we can write to it.
	 	 DataSink filewriter = null;
	 	 try {
	 	     filewriter = Manager.createDataSink(source, dest);
	 	     System.out.println("DataSink: " + filewriter);
	 	    System.out.println("DataSink content type: " + filewriter.getContentType());
	 	     filewriter.open();
	 	 } catch (NoDataSinkException e) {
	 		 e.printStackTrace();
	 	     System.exit(-1);
	 	 } catch (IOException e) {
	 		e.printStackTrace();
	 	     System.exit(-1);
	 	 } catch (SecurityException e) {
	 		e.printStackTrace();
	 	     System.exit(-1);
	 	 }

	 	 // if the Processor implements StreamWriterControl, we can
	 	 // call setStreamSizeLimit
	 	 // to set a limit on the size of the file that is written.
	 	 StreamWriterControl swc = (StreamWriterControl)
	 	     p.getControl("javax.media.control.StreamWriterControl");
	 	 //set limit to 5MB
	 	 if (swc != null)
	 	     swc.setStreamSizeLimit(5000000);
	 
	 	 // now start the filewriter and processor
	 	 try {
	 	     filewriter.start();
	 	 } catch (IOException e) {
	 		e.printStackTrace();
	 	     System.exit(-1);
	 	 }
	 	 // Capture for 5 seconds
	 	 sh.playToEndOfMedia(5000);
	 	 sh.close();
	 	 // Wait for an EndOfStream from the DataSink and close it...
	 	
	 	 // TODO: if the writing takes a long time, and is happening asynchronously, as it does in FMJ,
	 	 // the writing can theoretically get interrupted here.
	 	 
//	 	 System.out.println("Sleeping...");
//	 	try
//		{
//			Thread.sleep(5000);
//		} catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
	 	 
	 	 filewriter.close();


	 	 System.out.println("Done.");
	}
}
