package leveleditor.components;

public class FieldModel {
	private ButtonModel[][] model = new ButtonModel[3][3];
	
	public FieldModel(){
		for (int i = 0; i <3; i ++){
			for (int j = 0; j <3; j ++){
				setValue(i, j, 0);
			}
		}
	}
	
	public void setValue(int x, int y, int value){
		model[x][y]= new ButtonModel();
	}
	
	public ButtonModel getButtonModel(int x, int y){
		return model[x][y];
	}
}
