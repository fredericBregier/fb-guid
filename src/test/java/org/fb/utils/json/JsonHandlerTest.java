/*
 * Copyright (c) 2019-2022. FbUtilities Contributors and Frederic Bregier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 *  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 *   OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.fb.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class JsonHandlerTest {

  /**
   * Test method for {@link JsonHandler#createObjectNode()}.
   */
  @Test
  public void testCreateObjectNode() throws JsonProcessingException {
    final ObjectNode node = JsonHandler.createObjectNode();
    final String result = JsonHandler.writeAsString(node);
    final ObjectNode node2 = JsonHandler.getFromString(result);
    assertEquals(result, JsonHandler.writeAsString(node2));
    final Map<String, Object> map = JsonHandler.getMapFromString(result);
  }

  /**
   * Test method for {@link JsonHandler#createArrayNode()}.
   */
  @Test
  public void testCreateArrayNode() throws JsonProcessingException {
    final ArrayNode node = JsonHandler.createArrayNode();
    node.add(true);
    node.add("bytes".getBytes());
    node.add(2.0);
    node.add(3);
    node.add(5L);
    node.add("string");
    assertEquals(6, node.size());
    final String result = JsonHandler.writeAsString(node);
    ArrayNode node2;
    try {
      node2 = (ArrayNode) JsonHandler.mapper.readTree(result);
    } catch (final Exception e) {
      fail(e.getMessage());
      return;
    }
    assertEquals(result, JsonHandler.writeAsString(node2));
  }

  @Test
  public void extraTest() throws JsonProcessingException {
    String test =
        "{'X-Auth-User':'admin2','X-method':'OPTIONS','path':'/','base':'','cookie':{'another-cookie':'bar','my-cookie':'foo'},'answer':{'Allow':'GET,PUT,POST,DELETE,OPTIONS','UriAllowed':'control,transfers,hosts,config,hostconfigs,server,configurations,log,business,rules,info,bandwidth','DetailedAllow':[{'control':[{'GET':{'PATH':'/control','command':'GetTransferInformation','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.InformationJsonPacket','comment':'Information on Transfer request (GET)','requestUserPacket':0,'id':0,'request':0,'rulename':null,'filename':null,'idRequest':false,'to':false}}},{'PUT':{'PATH':'/control','command':'RestartTransfer','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.RestartTransferJsonPacket','comment':'Restart Transfer request (PUT)','requestUserPacket':0,'requester':'Requester host','requested':'Requested host','specialid':-9223372036854775808,'restarttime':1398677795527}}},{'PUT':{'PATH':'/control','command':'StopOrCancelTransfer','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.StopOrCancelJsonPacket','comment':'Stop Or Cancel request (PUT)','requestUserPacket':0,'requester':'Requester host','requested':'Requested host','specialid':-9223372036854775808}}},{'POST':{'PATH':'/control','command':'CreateTransfer','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.TransferRequestJsonPacket','comment':'Transfer Request (POST)','requestUserPacket':0,'rulename':'Rulename','mode':0,'filename':'Filename','requested':'Requested host','blocksize':0,'rank':0,'specialId':0,'validate':0,'originalSize':0,'fileInformation':'File information','separator':'{','start':1398677795531,'delay':0,'toValidate':true,'additionalDelay':false}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/control'}}]},{'transfers':[{'GET':{'command':'GET','PATH':'/transfers/id','SPECIALID':'Special Id in URI as transfers/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested'}},{'GET':{'command':'MULTIGET','PATH':'/transfers','json':{'LIMIT':'number','ORDERBYID':'boolean','STARTID':'transfer id','STOPID':'transfer id','IDRULE':'rule name','PARTNER':'PARTNER name','PENDING':'boolean','INTRANSFER':'boolean','ERROR':'boolean','DONE':'boolean','ALL':'boolean','STARTTRANS':'Date in ISO 8601 format or ms','STOPTRANS':'Date in ISO 8601 format or ms'}}},{'PUT':{'command':'UPDATE','PATH':'/transfers/id','SPECIALID':'Special Id in URI as transfers/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested','json':{'GLOBALSTEP':'INTEGER','GLOBALLASTSTEP':'INTEGER','STEP':'INTEGER','RANK':'INTEGER','STEPSTATUS':'VARCHAR','RETRIEVEMODE':'BIT','FILENAME':'VARCHAR','ISMOVED':'BIT','IDRULE':'VARCHAR','BLOCKSZ':'INTEGER','ORIGINALNAME':'VARCHAR','FILEINFO':'LONGVARCHAR','TRANSFERINFO':'LONGVARCHAR','MODETRANS':'INTEGER','STARTTRANS':'TIMESTAMP','STOPTRANS':'TIMESTAMP','INFOSTATUS':'VARCHAR','UPDATEDINFO':'INTEGER','OWNERREQ':'VARCHAR','REQUESTER':'VARCHAR','REQUESTED':'VARCHAR','SPECIALID':'BIGINT'}}},{'DELETE':{'command':'DELETE','PATH':'/transfers/id','SPECIALID':'Special Id in URI as transfers/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested'}},{'POST':{'command':'CREATE','PATH':'/transfers','json':{'GLOBALSTEP':'INTEGER','GLOBALLASTSTEP':'INTEGER','STEP':'INTEGER','RANK':'INTEGER','STEPSTATUS':'VARCHAR','RETRIEVEMODE':'BIT','FILENAME':'VARCHAR','ISMOVED':'BIT','IDRULE':'VARCHAR','BLOCKSZ':'INTEGER','ORIGINALNAME':'VARCHAR','FILEINFO':'LONGVARCHAR','TRANSFERINFO':'LONGVARCHAR','MODETRANS':'INTEGER','STARTTRANS':'TIMESTAMP','STOPTRANS':'TIMESTAMP','INFOSTATUS':'VARCHAR','UPDATEDINFO':'INTEGER','OWNERREQ':'VARCHAR','REQUESTER':'VARCHAR','REQUESTED':'VARCHAR','SPECIALID':'BIGINT'}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/transfers'}}]},{'hosts':[{'GET':{'command':'GET','PATH':'/hosts/id','HOSTID':'HostId in URI as hosts/id'}},{'GET':{'command':'MULTIGET','PATH':'/hosts','json':{'HOSTID':'host name','ADDRESS':'ADDRESS of this partner','ISSSL':'is Ssl entry','ISACTIVE':'is Active entry'}}},{'PUT':{'command':'UPDATE','PATH':'/hosts/id','HOSTID':'HostId in URI as hosts/id','json':{'ADDRESS':'VARCHAR','PORT':'INTEGER','ISSSL':'BIT','HOSTKEY':'VARBINARY','ADMINROLE':'BIT','ISCLIENT':'BIT','ISACTIVE':'BIT','ISPROXIFIED':'BIT','UPDATEDINFO':'INTEGER','HOSTID':'VARCHAR'}}},{'DELETE':{'command':'DELETE','PATH':'/hosts/id','HOSTID':'HostId in URI as hosts/id'}},{'POST':{'command':'CREATE','PATH':'/hosts','json':{'ADDRESS':'VARCHAR','PORT':'INTEGER','ISSSL':'BIT','HOSTKEY':'VARBINARY','ADMINROLE':'BIT','ISCLIENT':'BIT','ISACTIVE':'BIT','ISPROXIFIED':'BIT','UPDATEDINFO':'INTEGER','HOSTID':'VARCHAR'}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/hosts'}}]},{'config':[{'GET':{'PATH':'/config','command':'ExportConfig','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.ConfigExportJsonPacket','comment':'ConfigExport request (GET)','requestUserPacket':0,'host':false,'rule':false,'business':false,'alias':false,'roles':false}}},{'PUT':{'PATH':'/config','command':'ImportConfig','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.ConfigImportJsonPacket','comment':'ConfigImport request (PUT) where items are either set through transfer Id, either set directly with a filename','requestUserPacket':0,'purgehost':false,'purgerule':false,'purgebusiness':false,'purgealias':false,'purgeroles':false,'host':'HostFilename if not through TransferId','rule':'RuleFilename if not through TransferId','business':'BusinessFilename if not through TransferId','alias':'AliasFilename if not through TransferId','roles':'RolesFilename if not through TransferId','hostid':-9223372036854775808,'ruleid':-9223372036854775808,'businessid':-9223372036854775808,'aliasid':-9223372036854775808,'rolesid':-9223372036854775808}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/config'}}]},{'hostconfigs':[{'GET':{'command':'GET','PATH':'/hostconfigs/id','SPECIALID':'Special Id in URI as hostconfigs/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested'}},{'GET':{'command':'MULTIGET','PATH':'/hostconfigs','json':{'LIMIT':'number','ORDERBYID':'boolean','STARTID':'transfer id','STOPID':'transfer id','IDRULE':'rule name','PARTNER':'PARTNER name','PENDING':'boolean','INTRANSFER':'boolean','ERROR':'boolean','DONE':'boolean','ALL':'boolean','STARTTRANS':'Date in ISO 8601 format or ms','STOPTRANS':'Date in ISO 8601 format or ms'}}},{'PUT':{'command':'UPDATE','PATH':'/hostconfigs/id','SPECIALID':'Special Id in URI as hostconfigs/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested','json':{'GLOBALSTEP':'INTEGER','GLOBALLASTSTEP':'INTEGER','STEP':'INTEGER','RANK':'INTEGER','STEPSTATUS':'VARCHAR','RETRIEVEMODE':'BIT','FILENAME':'VARCHAR','ISMOVED':'BIT','IDRULE':'VARCHAR','BLOCKSZ':'INTEGER','ORIGINALNAME':'VARCHAR','FILEINFO':'LONGVARCHAR','TRANSFERINFO':'LONGVARCHAR','MODETRANS':'INTEGER','STARTTRANS':'TIMESTAMP','STOPTRANS':'TIMESTAMP','INFOSTATUS':'VARCHAR','UPDATEDINFO':'INTEGER','OWNERREQ':'VARCHAR','REQUESTER':'VARCHAR','REQUESTED':'VARCHAR','SPECIALID':'BIGINT'}}},{'DELETE':{'command':'DELETE','PATH':'/hostconfigs/id','SPECIALID':'Special Id in URI as hostconfigs/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested'}},{'POST':{'command':'CREATE','PATH':'/hostconfigs','json':{'GLOBALSTEP':'INTEGER','GLOBALLASTSTEP':'INTEGER','STEP':'INTEGER','RANK':'INTEGER','STEPSTATUS':'VARCHAR','RETRIEVEMODE':'BIT','FILENAME':'VARCHAR','ISMOVED':'BIT','IDRULE':'VARCHAR','BLOCKSZ':'INTEGER','ORIGINALNAME':'VARCHAR','FILEINFO':'LONGVARCHAR','TRANSFERINFO':'LONGVARCHAR','MODETRANS':'INTEGER','STARTTRANS':'TIMESTAMP','STOPTRANS':'TIMESTAMP','INFOSTATUS':'VARCHAR','UPDATEDINFO':'INTEGER','OWNERREQ':'VARCHAR','REQUESTER':'VARCHAR','REQUESTED':'VARCHAR','SPECIALID':'BIGINT'}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/hostconfigs'}}]},{'server':[{'PUT':{'PATH':'/server','command':'ShutdownOrBlock','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.ShutdownOrBlockJsonPacket','comment':'Shutdown Or Block request (PUT)','requestUserPacket':0,'key':'S2V5','shutdownOrBlock':false,'restartOrBlock':false}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/server'}}]},{'configurations':[{'GET':{'command':'GET','PATH':'/configurations/id','SPECIALID':'Special Id in URI as configurations/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested'}},{'GET':{'command':'MULTIGET','PATH':'/configurations','json':{'LIMIT':'number','ORDERBYID':'boolean','STARTID':'transfer id','STOPID':'transfer id','IDRULE':'rule name','PARTNER':'PARTNER name','PENDING':'boolean','INTRANSFER':'boolean','ERROR':'boolean','DONE':'boolean','ALL':'boolean','STARTTRANS':'Date in ISO 8601 format or ms','STOPTRANS':'Date in ISO 8601 format or ms'}}},{'PUT':{'command':'UPDATE','PATH':'/configurations/id','SPECIALID':'Special Id in URI as configurations/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested','json':{'GLOBALSTEP':'INTEGER','GLOBALLASTSTEP':'INTEGER','STEP':'INTEGER','RANK':'INTEGER','STEPSTATUS':'VARCHAR','RETRIEVEMODE':'BIT','FILENAME':'VARCHAR','ISMOVED':'BIT','IDRULE':'VARCHAR','BLOCKSZ':'INTEGER','ORIGINALNAME':'VARCHAR','FILEINFO':'LONGVARCHAR','TRANSFERINFO':'LONGVARCHAR','MODETRANS':'INTEGER','STARTTRANS':'TIMESTAMP','STOPTRANS':'TIMESTAMP','INFOSTATUS':'VARCHAR','UPDATEDINFO':'INTEGER','OWNERREQ':'VARCHAR','REQUESTER':'VARCHAR','REQUESTED':'VARCHAR','SPECIALID':'BIGINT'}}},{'DELETE':{'command':'DELETE','PATH':'/configurations/id','SPECIALID':'Special Id in URI as configurations/id','REQUESTER':'Partner as requester','REQUESTED':'Partner as requested'}},{'POST':{'command':'CREATE','PATH':'/configurations','json':{'GLOBALSTEP':'INTEGER','GLOBALLASTSTEP':'INTEGER','STEP':'INTEGER','RANK':'INTEGER','STEPSTATUS':'VARCHAR','RETRIEVEMODE':'BIT','FILENAME':'VARCHAR','ISMOVED':'BIT','IDRULE':'VARCHAR','BLOCKSZ':'INTEGER','ORIGINALNAME':'VARCHAR','FILEINFO':'LONGVARCHAR','TRANSFERINFO':'LONGVARCHAR','MODETRANS':'INTEGER','STARTTRANS':'TIMESTAMP','STOPTRANS':'TIMESTAMP','INFOSTATUS':'VARCHAR','UPDATEDINFO':'INTEGER','OWNERREQ':'VARCHAR','REQUESTER':'VARCHAR','REQUESTED':'VARCHAR','SPECIALID':'BIGINT'}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/configurations'}}]},{'log':[{'GET':{'PATH':'/log','command':'GetLog','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.LogJsonPacket','comment':'Log export request (GET)','requestUserPacket':0,'purge':false,'clean':false,'statuspending':false,'statustransfer':false,'statusdone':false,'statuserror':false,'rule':'The rule name','request':'The requester or requested host name','start':1398677795544,'stop':1398677795544,'startid':'Start id - long -','stopid':'Stop id - long -'}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/log'}}]},{'business':[{'GET':{'PATH':'/business','command':'ExecuteBusiness','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.BusinessRequestJsonPacket','comment':'Business execution request (GET)','requestUserPacket':0,'className':'Class name to execute','arguments':'Arguments of the execution','extraArguments':'Extra arguments','delay':0,'toApplied':false,'validated':false}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/business'}}]},{'rules':[{'GET':{'command':'GET','PATH':'/rules/id','IDRULE':'RuleId in URI as rules/id'}},{'GET':{'command':'MULTIGET','PATH':'/rules','json':{'IDRULE':'rule name','MODETRANS':'MODETRANS value'}}},{'PUT':{'command':'UPDATE','PATH':'/rules/id','IDRULE':'RuleId in URI as rules/id','json':{'HOSTIDS':'LONGVARCHAR','MODETRANS':'INTEGER','RECVPATH':'VARCHAR','SENDPATH':'VARCHAR','ARCHIVEPATH':'VARCHAR','WORKPATH':'VARCHAR','RPRETASKS':'LONGVARCHAR','RPOSTTASKS':'LONGVARCHAR','RERRORTASKS':'LONGVARCHAR','SPRETASKS':'LONGVARCHAR','SPOSTTASKS':'LONGVARCHAR','SERRORTASKS':'LONGVARCHAR','UPDATEDINFO':'INTEGER','IDRULE':'VARCHAR'}}},{'DELETE':{'command':'DELETE','PATH':'/rules/id','IDRULE':'RuleId in URI as rules/id'}},{'POST':{'command':'CREATE','PATH':'/rules','json':{'HOSTIDS':'LONGVARCHAR','MODETRANS':'INTEGER','RECVPATH':'VARCHAR','SENDPATH':'VARCHAR','ARCHIVEPATH':'VARCHAR','WORKPATH':'VARCHAR','RPRETASKS':'LONGVARCHAR','RPOSTTASKS':'LONGVARCHAR','RERRORTASKS':'LONGVARCHAR','SPRETASKS':'LONGVARCHAR','SPOSTTASKS':'LONGVARCHAR','SERRORTASKS':'LONGVARCHAR','UPDATEDINFO':'INTEGER','IDRULE':'VARCHAR'}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/rules'}}]},{'info':[{'GET':{'PATH':'/info','command':'GetInformation','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.InformationJsonPacket','comment':'Information request (GET)','requestUserPacket':0,'id':0,'request':0,'rulename':'The rule name associated with the remote repository','filename':'The filename to look for if any','idRequest':false,'to':false}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/info'}}]},{'bandwidth':[{'GET':{'PATH':'/bandwidth','command':'GetBandwidth','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.BandwidthJsonPacket','comment':'Bandwidth getter (GET)','requestUserPacket':0,'setter':false,'writeglobal':-10,'readglobal':-10,'writesession':-10,'readsession':-10}}},{'PUT':{'PATH':'/bandwidth','command':'SetBandwidth','json':{'@class':'org.waarp.openr66.protocol.localhandler.packet.json.BandwidthJsonPacket','comment':'Bandwidth setter (PUT)','requestUserPacket':0,'setter':false,'writeglobal':-10,'readglobal':-10,'writesession':-10,'readsession':-10}}},{'OPTIONS':{'command':'OPTIONS','PATH':'/bandwidth'}}]}]}}";
    test = test.replace("'", "\"");
    final ObjectNode node2 = JsonHandler.getFromString(test);
    assertNotNull(node2);
    final String test2 = JsonHandler.writeAsString(node2);
    assertEquals(test, test2);
    final Map<String, Object> map = JsonHandler.getMapFromString(test);
    assertNotNull(map);
  }

  @Test
  public void testGetFromStringExcBadFormat() throws JsonProcessingException {
    final String badFormat = "{\"foo\":\"bar\",\"baz\"";

    assertThrows(JsonProcessingException.class, () -> JsonHandler.getFromString(badFormat));
  }

  @Test
  public void testMapRendering() throws JsonProcessingException {
    final String maps = "{\"foo\": \"bar\", \"foo2\": true}";
    final String maps2 = "{\"foo\":\"bar\",\"foo2\":true}";
    String unescaped = JsonHandler.unEscape(maps);
    assertEquals(maps, unescaped);
    Map<String, Object> map = JsonHandler.getMapFromString(maps);
    assertEquals(true, map.get("foo2"));
    assertEquals("bar", map.get("foo"));
    String escaped = JsonHandler.writeAsStringEscaped(map);
    assertEquals("{\"foo\":\"bar\",\"foo2\":true}", escaped);
    String unescaped2 = JsonHandler.unEscape(escaped);
    assertEquals(maps2, unescaped2);

    final String maps3 = "{\\\\\"foo\": \"bar\", \"foo2\": true}";
    unescaped = JsonHandler.unEscape(maps3);
    assertEquals(maps, unescaped);
    map = JsonHandler.getMapFromString(maps);
    assertEquals(true, map.get("foo2"));
    assertEquals("bar", map.get("foo"));
    escaped = JsonHandler.writeAsStringEscaped(map);
    assertEquals("{\"foo\":\"bar\",\"foo2\":true}", escaped);
    unescaped2 = JsonHandler.unEscape(escaped);
    assertEquals(maps2, unescaped2);
  }

  @Test
  public void testWithFile() throws IOException {
    final String maps = "{\"foo\": \"bar\", \"foo2\": true}";
    String unescaped = JsonHandler.unEscape(maps);
    assertEquals(maps, unescaped);
    Map<String, Object> map = JsonHandler.getMapFromString(maps);
    File fileTmp = File.createTempFile("Json", ".test");
    JsonHandler.writeAsFile(map, fileTmp);
    Map<String, Object> map2 = (Map<String, Object>) JsonHandler.getFromFile(fileTmp, Map.class);
    assertEquals(map, map2);
    ObjectNode map3 = JsonHandler.getFromFile(fileTmp);
    assertEquals(JsonHandler.prettyPrint(map), JsonHandler.prettyPrint(map3));

  }

  private enum FIELDS {
    bytes, string, tboolean, tdouble, tint, tlong
  }
}
