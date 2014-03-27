package dr;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

public class JUtil {
	public static String TrimString(String str, int mode){
		if(mode == 0){
			return str.trim();
		}
		if(mode < 0){
			return str.replaceFirst(" \t\n\r\f", "");				
			
		}
		else{
			return str.replaceFirst(" \t\n\r\f$", "");
		}
	}
	public static String EraseString(String str, String subString) {
		String newCopy;
		int index = str.indexOf(subString);
		if(index == -1){
			return str;
		}
		String prefix_String = str.substring(0,index);
		String suffix_String = str.substring(index+subString.length(), str.length());
		newCopy = prefix_String + suffix_String;
		return newCopy;
	}
	
	public static String ToLower(String str){
		return str.toLowerCase();
	}
	
	/*!
	* Finds the keyword in the string starting from start to end.
	*
	* \param str string
	* \param keyword keyword to find
	* \param start starting index for search
	* \param end ending index for search
	* \param casesensitive is case sensitive?
	*
	* \return index of keyword in string
	*/
	
	public static int FindKeyWord(String str, String keyword, int start, int end, boolean casesensitive)
	{
		String SPECIAL_CHARS=" \t;[]()+/-*<>=,&~!^?:%{}|";
		int kw_length=keyword.length();
		int idx, i=start;
		String str1 =new String(str);
		String keyword1=new String(keyword);
		
		if(end>str1.length()-1)
			end=str1.length()-1; //inclusive
		
		if(!casesensitive)
		{
			
			str1=JUtil.ToLower(str1);
			keyword1=JUtil.ToLower(keyword1);
		}
		
		while(i<=end)
		{
			idx=str1.indexOf(keyword1, i);
			if(idx!=-1&&idx+kw_length-1<=end)
			{
				
				if ((idx == 0 || strchr(SPECIAL_CHARS, str1.charAt(idx-1)) != false) &&
						(idx + kw_length >= str1.length() ||
						strchr(SPECIAL_CHARS, str1.charAt(idx + kw_length))!= false))
				{
					// the keyword stands alone or surrounded by special chars
					return idx;
				}
				
			}
			else
			{
				// cannot find the keyword in str
				break;
				
			}
			
			
			i=idx+1;
		}
		return -1;
		
	}
	
	private static  boolean strchr( String t, char a)
	{
		for(char ch: t.toCharArray())
		{
			if(a==ch)
				return true;
			
		}
		return false;
		
	}
	
	/*!
	* Counts keywords in string.
	*
	* \param base string to search
	* \param container set of keywords
	* \param count number of keywords found
	* \param mode allowable leading/trailing character mode (1=exclude keywords surrounded by exclude characters, 2=include only keywords preceded by include1 characters and followed by include2 characters)
	* \param exclude list of characters that may not surround keywords found (if mode=1)
	* \param include1 list of characters that must precede keywords found (if mode=2)
	* \param include2 list of characters that must follow keywords found (if mode=2)
	* \param counter_container stores list of individual keyword counts (if specified)
	* \param casesensitive language is case sensitive?
	*/
	public static void CountTally(String base,ArrayList<String> container,  long count, int mode, String exclude, String include1, String include2, ArrayList<Long> counter_container, boolean casesensitive)
	{
		int idx=0;
		String base1;
		Iterator<String> vit=container.iterator();
		Iterator<Long> cit=counter_container.iterator();
		long single_count=0;
		base1=" "+base+" ";
		
		if(casesensitive==false)
		{
			base1=JUtil.ToLower(base1);
			//iterator
			while(vit.hasNext())
			{
				String str=vit.next();
				str=JUtil.ToLower(str);
				
			}
			
			
		}
		
		if(mode==1)
		{
			while(vit.hasNext())
			{
				String str1=vit.next();
				idx=base1.indexOf(str1);
				while(idx!=-1)
				{
					if((exclude.indexOf(str1.charAt(idx+str1.length()))==-1)&&(exclude.indexOf(str1.charAt(idx-1)))==-1)
					{
						count++;
						single_count++;
					}
				idx=base1.indexOf(str1, idx+str1.length());		
				}
				
				if (counter_container!=null)
				{
					if(cit.hasNext())
					{
						Long curr=cit.next();
						curr+=single_count;
					}
					//(*cit) += single_count;
					single_count = 0;
					
				}
				
			}
			
		}
		else if(mode==2)
		{
			
			while(vit.hasNext())
			{
				String str1=vit.next();
				idx=base1.indexOf(str1);
				while(idx!=-1)
				{
					if((include1.indexOf(str1.charAt(idx+str1.length()))==-1)
							&&(include2.indexOf(str1.charAt(idx-1)))==-1)
					{
						count++;
						
					}
				idx=base1.indexOf(str1, idx+str1.length());		
				}
			}
				
			
			
			
			
		}
		
		
		
	}
	
	/*!
	* Extracts the filename (without path) from the filepath.
	* ex. abc\xyz.cpp --> xyz.abc
	*
	* \param filepath file path
	*
	* \return file name
	*/
	public static String ExtractFilename(String filepath)
	{
		int idx=filepath.lastIndexOf("\\");
		if (idx == -1)
			idx = filepath.lastIndexOf("/");	// Unix
		if (idx != -1)
			return filepath.substring(idx+1);
		return filepath;
		
	}
	
	
	
	/*!
	* For a given directory name, extract all the files from that directory as well as
	* from all its sub-directories and store the filenames in the fileList vector.
	*
	* \param folder folder to list
	* \param fileExtList list of file extensions to search
	* \param fileList list of files in folder
	* \param symLinks follow Unix links?
	*
	* \return path exists and is a directory
	*/
	
	public static boolean ListAllFiles(String folder, ArrayList<String> fileExtList, ArrayList<String> fileList,  boolean symLinks)
	{
		
		ArrayList<String> tmpList;
		String file;
		int i, n;
		folder=JUtil.TrimString(folder,0);
		
/*	#ifdef UNIX
		// skip links if user specified
		struct stat inodeData;
		if (!symLinks && (lstat(folder.c_str(), &inodeData) < 0 || S_ISLNK(inodeData.st_mode)))
			return(false);
		#endif*/
		
		//private static final boolean enableFast = "true".equals(System.getProperty("fast")
		if(UNIX)
		{
			Path path=(Paths.get(folder));
			if(!symLinks&&(path!=null)||Files.getPosixFilePermissions(path)!=null)
			return false;
			
		}
		
		// process folder
		if (!GetFileList(tmpList, folder, symLinks))
			return false;
		
		

		// read through tmpList and get the names of all the files in the directory mentioned
		for (n = 0; n < tmpList.size(); n++)
		{
			file = tmpList.get(n);

			// if no-extension filtering, each file is pushed into the fileList
			if (fileExtList.get(0) .equals("*.*") || fileExtList.get(0).equals("*"))
				fileList.add(file);
			else
			{
				// for each extension, if file extension matches with the extension, the file is pushed into the fileList
				for (i = 0; i < fileExtList.size(); i++)
				{
					if (MatchFilename(ExtractFilename(file), fileExtList.get(i)))
						fileList.add(file);
				}
			}
		}
		tmpList.clear();
		return true;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String a = "efg";
		String sub = "asd";
		
		//String res = TrimString(a, 0);
		String res = EraseString(a,sub);
		System.out.println(res);
	}

}

