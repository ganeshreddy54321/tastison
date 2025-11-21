import java.io.FileReader;
import java.io.IOException;

class f2{
	public static void main(String[] args){
		try(FileReader r = new FileReader("output.txt")){
			int ch;
			while((ch=r.read())!=-1){
				System.out.println((char)ch);
			}
		}
		catch(IOException e){
			System.out.println("An error occurred while reading the file:");
			e.printStackTrace();
		}
	}
}
			 