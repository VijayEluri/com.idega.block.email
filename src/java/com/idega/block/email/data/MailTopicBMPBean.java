package com.idega.block.email.data;

import com.idega.block.email.business.EmailTopic;
import com.idega.data.IDOLegacyEntity;
import java.sql.Timestamp;

/**
 *  Title: Description: Copyright: Copyright (c) 2001 Company:
 *
 * @author     <br>
 *      <a href="mailto:aron@idega.is">Aron Birkir</a> <br>
 *
 * @created    14. mars 2002
 * @version    1.0
 */

public class MailTopicBMPBean extends com.idega.data.CategoryEntityBMPBean implements com.idega.block.email.data.MailTopic,com.idega.block.email.business.EmailTopic {

  /**
   * @todo    Description of the Field
   */
  public final static String TABLE_NAME = "em_topic";
  /**
   * @todo    Description of the Field
   */
  public final static String GROUP = "em_group_id";
  /**
   * @todo    Description of the Field
   */
  public final static String LIST = "em_list_id";
  /**
   * @todo    Description of the Field
   */
  public final static String NAME = "name";
  /**
   * @todo    Description of the Field
   */
  public final static String DESCRIPTION = "description";
  /**
   * @todo    Description of the Field
   */
  public final static String CREATED = "created";


  /**  Constructor for the Topic object */
  public MailTopicBMPBean() {
    super();
  }


  /**
   *  Constructor for the Topic object
   *
   * @param  id                         Description of the Parameter
   * @exception  java.sql.SQLException  Description of the Exception
   */
  public MailTopicBMPBean(int id) throws java.sql.SQLException {
    super(id);
  }


  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(GROUP, "Group id", true, true, Integer.class, MANY_TO_ONE, MailGroup.class);
    addAttribute(LIST, "List id", true, true, Integer.class, MANY_TO_ONE, MailList.class);
    addAttribute(NAME, "Name", true, true, String.class);
    addAttribute(DESCRIPTION, "Description", true, true, String.class);
    addAttribute(CREATED, "created", true, true, Timestamp.class);
    addManyToManyRelationShip(MailAccount.class);
    addManyToManyRelationShip(MailLetter.class);
  }


  /**
   *  Gets the entityName of the Topic object
   *
   * @return    The entity name value
   */
  public String getEntityName() {
    return TABLE_NAME;
  }


  /**
   *  Gets the letterGroupId of the Topic object
   *
   * @return    The letter group id value
   */
  public int getGroupId() {
    return getIntColumnValue(GROUP);
  }


  /**
   *  Sets the letterGroupId attribute of the Topic object
   *
   * @param  groupId  The new letterGroupId value
   */
  public void setGroupId(int groupId) {
    setColumn(GROUP, groupId);
  }


  /**
   *  Gets the listId of the Topic object
   *
   * @return    The list id value
   */
  public int getListId() {
    return getIntColumnValue(LIST);
  }


  /**
   *  Sets the listId attribute of the Topic object
   *
   * @param  listId  The new listId value
   */
  public void setListId(int listId) {
    setColumn(LIST, listId);
  }


  /**
   *  Gets the name of the Topic object
   *
   * @return    The name value
   */
  public String getName() {
    return getStringColumnValue(NAME);
  }


  /**
   *  Gets the description of the Topic object
   *
   * @return    The description value
   */
  public String getDescription() {
    return getStringColumnValue(DESCRIPTION);
  }


  /**
   *  Gets the created of the Topic object
   *
   * @return    The created value
   */
  public Timestamp getCreated() {
    return (Timestamp) getColumnValue(CREATED);
  }


  /**
   *  Sets the name attribute of the Topic object
   *
   * @param  name  The new name value
   */
  public void setName(String name) {
    setColumn(NAME, name);
  }


  /**
   *  Sets the description attribute of the Topic object
   *
   * @param  description  The new description value
   */
  public void setDescription(String description) {
    setColumn(DESCRIPTION, description);
  }


  /**
   *  Sets the created attribute of the Topic object
   *
   * @param  created  The new created value
   */
  public void setCreated(Timestamp created) {
    setColumn(CREATED, created);
  }
}

