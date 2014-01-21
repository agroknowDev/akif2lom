package gr.agroknow.metadata.transformer.akif2lom;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class AKIF2LOM
{
	private JSONObject akifObject ;
	private StringBuilder lom ;
	
	public AKIF2LOM( String akifString )
	{
		akifObject = (JSONObject) JSONValue.parse( akifString ) ;
		lom = new StringBuilder() ;
		header() ;
		general() ;
		lifeCycle() ;
		technical() ;
		educational() ;
		rights() ;
		relation() ;
		classification() ;
		footer() ;
	}
	
	public String toString()
	{
		return lom.toString() ;
	}
	
	private void header()
	{
		lom.append( "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lomLoose.xsd\">\n" ) ;
	}
	
	private void general()
	{
		List<LanguageString> title = new ArrayList<LanguageString>() ;
		List<LanguageString> description = new ArrayList<LanguageString>() ;
		List<LanguageString> keywords = new ArrayList<LanguageString>() ;
		List<LanguageString> coverage = new ArrayList<LanguageString>() ;
		
		lom.append( "<general>\n" ) ;
		lom.append( "	<identifier>\n" ) ;
		lom.append( "		<catalog>AKIF</catalog>\n" ) ;
		lom.append( "		<entry>" + (Long)akifObject.get( "identifier" )  + "</entry>\n" ) ;
		lom.append( "	</identifier>\n" ) ;
		JSONObject languageBlocks = (JSONObject)akifObject.get( "languageBlocks" ) ;
		for ( Object keyObject: languageBlocks.keySet() )
		{
			String key = (String)keyObject ;
			JSONObject block = (JSONObject) languageBlocks.get( keyObject ) ;
			if ( block.get( "title" ) != null )
			{
				title.add( new LanguageString( key, (String)block.get( "title" ) ) ) ;
			}
			if ( block.get( "description" ) != null )
			{
				description.add( new LanguageString( key, (String)block.get( "description" ) ) ) ;
			}
			if ( block.get( "coverage" ) != null )
			{
				coverage.add( new LanguageString( key, (String)block.get( "coverage" ) ) ) ;
			}
			if ( block.get( "keywords" ) != null )
			{
				JSONArray kws = (JSONArray)block.get( "keywords" ) ;
				for( Object oKeyword: kws )
				{
					keywords.add( new LanguageString( key, (String)oKeyword ) ) ;
				}
			}
		}
		if ( title.size() > 0 )
		{
			lom.append( "	<title>\n" ) ;
			for( LanguageString ls: title )
			{
				lom.append( "		<string language=\"" + ls.getLanguage() + "\">" + ls.getString() + "</string>\n" ) ;
			}
			lom.append( "	</title>\n" ) ;
		}
		JSONArray expressions = (JSONArray)akifObject.get( "expressions" ) ;
		if( expressions.size() > 1 )
		{
			System.err.println( "Warning: This learning object is available in more than one language !" ) ;
		}
		JSONObject expression = (JSONObject)expressions.get( 0 ) ;
		String language = (String)expression.get( "language" ) ;
		lom.append( "	<language>" + language + "</language>\n" ) ;
		if ( description.size() > 0 )
		{
			lom.append( "	<description>\n" ) ;
			for( LanguageString ls: description )
			{
				lom.append( "		<string language=\"" + ls.getLanguage() + "\">" + ls.getString() + "</string>\n" ) ;
			}
			lom.append( "	</description>\n" ) ;
		}
		//
		if ( keywords.size() > 0 )
		{
			for( LanguageString ls: keywords )
			{
				lom.append( "	<keyword>\n" ) ;
				lom.append( "		<string language=\"" + ls.getLanguage() + "\">" + ls.getString() + "</string>\n" ) ;
				lom.append( "	</keyword>\n" ) ;
			}
		}
		//
		if (coverage.size() > 0)
		{
			lom.append( "	<coverage>\n" ) ;
			for( LanguageString ls: coverage )
			{
				lom.append( "		<string language=\"" + ls.getLanguage() + "\">" + ls.getString() + "</string>\n" ) ;
			}
			lom.append( "	</coverage>\n" ) ;
		}
		lom.append( "</general>\n" ) ;
	}
	
	private void lifeCycle()
	{
		JSONArray contributors = (JSONArray)akifObject.get( "contributors" ) ;
		if ( !contributors.isEmpty() )
		{
			lom.append( "<lifeCycle>\n" ) ;
			for ( Object object: contributors )
			{
				// System.out.println( (String)object ) ;
				JSONObject contribute = (JSONObject)object ;
				lom.append( "	<contribute>\n" ) ;
				if ( contribute.get( "role" ) != null )
				{
					lom.append( "		<role>\n" ) ;
					lom.append( "			<source>LRE.roleValues</source>\n" ) ;
					lom.append( "			<value>" + (String)contribute.get( "role" ) + "</value>\n" ) ;
					lom.append( "		</role>\n" ) ;
				}
				if ( ( contribute.get( "name" ) != null ) || ( contribute.get( "organization" ) != null ) )
				{
					lom.append( "		<entity><![CDATA[begin:vcard\n" ) ;
					lom.append( "VERSION:3.0\n" ) ;
					if ( contribute.get( "name" ) != null )
					{
						lom.append( "N:" + (String)contribute.get( "name" ) + "\n" ) ;
					}
					if ( contribute.get( "organization" ) != null )
					{
						lom.append( "ORG: " + (String)contribute.get( "organization" ) + "\n" ) ;
					}
					lom.append( "end:vCard]]></entity>\n" ) ;
				}
				if ( contribute.get( "date" ) != null )
				{
					lom.append( "		<date>\n" ) ;
					lom.append( "			<dateTime>" + (String)contribute.get( "date" ) + "</dateTime>\n" ) ;
					lom.append( "		</date>\n" ) ;
				}
				lom.append( "	</contribute>\n" ) ;
			}
			lom.append( "</lifeCycle>\n" ) ;
		}
	}
	
	private void technical()
	{
		if ( akifObject.containsKey( "expressions" ) )
		{
			lom.append( "<technical>\n" ) ;
			JSONArray expressions = (JSONArray)akifObject.get( "expressions" ) ;
			if( expressions.size() > 1 )
			{
				System.err.println( "Warning: This learning object has more than one expression !" ) ;
			}
			JSONObject expression = (JSONObject)expressions.get( 0 ) ;
			JSONArray manifestations = (JSONArray)expression.get( "manifestations" ) ;
			if( manifestations.size() > 1 )
			{
				System.err.println( "Warning: This learning object has more than one manifestation !" ) ;
			}
			JSONObject manifestation = (JSONObject)manifestations.get( 0 ) ;			
                        JSONArray items = (JSONArray)manifestation.get( "items" ) ;
			for( Object it : items )
			{
				JSONObject item = (JSONObject) it ;
				lom.append( "	<location>" + (String) item.get("url") + "</location>\n" ) ;
			}
                        lom.append( "	<format>" + (String) manifestation.get("parameter") + "</format>\n" ) ;
			lom.append( "</technical>\n" ) ;
		}
	}

	private void educational()
	{
		JSONObject tokenBlock = (JSONObject) akifObject.get( "tokenBlock" ) ;
		if ( !tokenBlock.isEmpty() )
		{
			lom.append( "<educational>\n" ) ;
			if (tokenBlock.containsKey( "learningResourceTypes") )
			{
				JSONArray lrts = (JSONArray) tokenBlock.get( "learningResourceTypes" ) ;
				for ( Object obj : lrts )
				{
					lom.append( "	<learningResourceType>\n" ) ;
					lom.append( "		<source>LRE.learningResourceTypeValues</source>\n" ) ;
					lom.append( "		<value>" + (String)obj + "</value>\n" ) ;
					lom.append( "	</learningResourceType>\n" ) ;
				}
			}
			if (tokenBlock.containsKey( "endUserRoles" ) )
			{
				JSONArray eurs = (JSONArray) tokenBlock.get( "endUserRoles" ) ;
				for ( Object obj : eurs )
				{
					lom.append( "	<intendedEndUserRole>\n" ) ;
					lom.append( "		<source>LRE.intendedEndUserRoleValues</source>\n" ) ;
					lom.append( "		<value>" + (String)obj + "</value>\n" ) ;
					lom.append( "	</intendedEndUserRole>\n" ) ;
				}
			}
			if (tokenBlock.containsKey( "contexts") )
			{
				JSONArray ctxs = (JSONArray) tokenBlock.get( "contexts" ) ;
				for ( Object obj : ctxs )
				{
					lom.append( "	<context>\n" ) ;
					lom.append( "		<source>LRE.contextValues</source>\n" ) ;
					lom.append( "		<value>" + (String)obj + "</value>\n" ) ;
					lom.append( "	</context>\n" ) ;
				}
			}
			if (tokenBlock.containsKey( "ageRange" ) )
			{
					lom.append( "	<typicalAgeRange>\n" ) ;
					lom.append( "		<string language=\"en\">" + (String)tokenBlock.get( "ageRange" ) + "</string>\n" ) ;
					lom.append( "	</typicalAgeRange>\n" ) ;
			}
			lom.append( "</educational>\n" ) ;
		}
		
	}
	
	
	private void rights()
	{
		JSONObject rights = (JSONObject) akifObject.get( "rights" ) ;
		if ( ( rights != null ) && !rights.isEmpty() )
		{
			lom.append( "<rights>\n" ) ;
			lom.append( "	<description>\n" ) ;
			if (rights.containsKey( "url" ) )
			{
				lom.append( "		<string language=\"x-t-url\">" + (String)rights.get( "url" ) + "</string>\n" ) ;
			}
			if (rights.containsKey( "description" ) )
			{
				JSONObject description = (JSONObject) rights.get( "description" ) ;
				for ( Object language: description.keySet() )
				{
					lom.append( "		<string language=\"" + (String)language + "\">" + (String)description.get( language ) + "</string>\n" ) ;
				}
			}
			lom.append( "	</description>\n" ) ;
			lom.append( "</rights>\n" ) ;
		}
	}
	
	private void relation()
	{
		if ( akifObject.containsKey( "learningObjectives" ) )
		{
			JSONObject objectives = (JSONObject)akifObject.get( "learningObjectives" ) ;
			if ( objectives.containsKey( "Agricom competences" ) )
			{
				JSONArray competences = (JSONArray)objectives.get( "Agricom competences" ) ;
				for ( Object obj : competences )
				{
					lom.append( "<relation>\n" ) ;
					lom.append( "	<kind>\n" ) ;
					lom.append( "		<source>LREv3.0</source>\n" ) ;
					lom.append( "		<value>requires</value>\n" ) ;
					lom.append( "	</kind>\n" ) ;
					lom.append( "	<resource>\n" ) ;
					lom.append( "		<identifier>\n" ) ;
					lom.append( "			<catalog>Agricom competences</catalog>\n" ) ;
					lom.append( "			<entry>" + (String)obj + "</entry>\n" ) ;
					lom.append( "		</identifier>\n" ) ;
					lom.append( "	</resource>\n" ) ;
					lom.append( "</relation>\n" ) ;
				}
			}
		}
	}

	private void classification()
	{
		if ( akifObject.containsKey("tokenBlock") )
		{
			JSONObject tokenBlock = (JSONObject)akifObject.get( "tokenBlock" ) ;
			if ( tokenBlock.containsKey("taxonPaths") )
			{
				JSONObject taxonPath = (JSONObject)tokenBlock.get( "taxonPaths" ) ;
				if (!taxonPath.isEmpty() )
				{
					for (Object source: taxonPath.keySet() )
					{
						JSONArray taxons = (JSONArray)taxonPath.get( source ) ;
						for( Object taxon: taxons )
						{
							lom.append( "<classification>\n" ) ;
							// lom.append( "	<purpose>\n" ) ;
							// lom.append( "		<source>purposeValues</source>\n" ) ;
							// lom.append( "		<value>discipline</value>\n" ) ;
							// lom.append( "	</purpose>\n" ) ;
							lom.append( "	<taxonPath>\n" ) ;
							lom.append( "		<source>\n" ) ;
							lom.append( "			<string>" + (String)source + "</string>\n" ) ;
							lom.append( "		</source>\n" ) ;
							lom.append( "		<taxon>\n" ) ;
							lom.append( "			<id>" + (String)taxon + "</id>\n" ) ;
							lom.append( "		</taxon>\n" ) ;
							lom.append( "	</taxonPath>\n" ) ;			
							lom.append( "</classification>\n" ) ;	
						}
					}
				}
			}
		}
	}
	
	private void footer()
	{
		lom.append( "</lom>\n" ) ;
	}
}
