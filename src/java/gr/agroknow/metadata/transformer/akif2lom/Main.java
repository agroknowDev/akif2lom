package gr.agroknow.metadata.transformer.akif2lom;
import java.io.File;
import org.apache.commons.io.FileUtils;

public class Main
{
	
	public static void main(String[] args)
	{
		if ( args.length != 2 )
		{
			System.err.println( "Usage : java -jar akif2lom.jar <INPUT_FOLDER> <OUTPUT_FOLDER>" ) ;
			System.exit( -1 ) ;
		}
		String inputFolder = args[0] ;
		String outputFolder = args[1] ;
		
		AKIF2LOM transformer = null ;
		int identifier = 0 ;
		File inputDirectory = new File( inputFolder ) ;
		String akifString = null ;
		int wrong = 0 ;
		for (File akifFile: inputDirectory.listFiles() )
		{
			try
			{
				identifier = Integer.parseInt( akifFile.getName().substring(0, akifFile.getName().length()-5 ) ) ;
				akifString = FileUtils.readFileToString( akifFile ) ;
				transformer = new AKIF2LOM( akifString ) ;
				FileUtils.writeStringToFile( new File( outputFolder + File.separator + identifier + ".xml" ) , transformer.toString() ) ;
			}
			catch( Exception e )
			{
				wrong++ ;
				System.err.println( "Wrong file : " + identifier ) ;
				//e.printStackTrace() ;
				//System.exit( identifier ) ;
			}
		}
		System.out.println( "#wrong : " + wrong ) ;
	}

}
