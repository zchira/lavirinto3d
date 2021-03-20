package leveleditor.components;

public class TableModel {
	private FieldModel[][] model;
	
	private int width;
	
	private int height;
	
	public TableModel(int x, int y){
		width = x;
		height = y;
		model = new FieldModel[x][y];
		init();
	}
	
	
	
	private void init(){
		for (int i = 0; i <height; i ++){
			for (int j = 0; j <width; j ++){
				setValue(j, i, new FieldModel());
			}
		}
	}
	
	private void setValue(int x, int y, FieldModel m){
		model[x][y] = m;
	}



	public int getWidth() {
		return width;
	}



//	private void setWidth(int width) {
//		this.width = width;
//	}



	public int getHeight() {
		return height;
	}



//	private void setHeight(int height) {
//		this.height = height;
//	}
}
