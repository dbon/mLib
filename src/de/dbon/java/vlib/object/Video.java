package de.dbon.java.vlib.object;


public class Video {

  private long id;
  private String name;
  private String extension;
  private String path;
  private long filesize;
  private String lastviewed;
  private int viewcount;
  private String tags;

  @Override
  public String toString() {
    return "Video [name=" + name + ", extension=" + extension + ", path=" + path + ", filesize="
        + filesize + ", lastviewed=" + lastviewed + ", viewcount=" + viewcount + ", tags=" + tags
        + "]";
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public long getFilesize() {
    return filesize;
  }

  public void setFilesize(long filesize) {
    this.filesize = filesize;
  }

  public String getLastviewed() {
    return lastviewed;
  }

  public void setLastviewed(String lastviewed) {
    this.lastviewed = lastviewed;
  }

  public int getViewcount() {
    return viewcount;
  }

  public void setViewcount(int viewcount) {
    this.viewcount = viewcount;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }



}
