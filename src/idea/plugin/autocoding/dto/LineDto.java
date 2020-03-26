package idea.plugin.autocoding.dto;

public class LineDto {
    private String className;
    private String instanceName;
    private Integer lineNum;
    private Integer startPosition;
    private String preSpace;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public String getPreSpace() {
        return preSpace;
    }

    public void setPreSpace(String preSpace) {
        this.preSpace = preSpace;
    }
}
