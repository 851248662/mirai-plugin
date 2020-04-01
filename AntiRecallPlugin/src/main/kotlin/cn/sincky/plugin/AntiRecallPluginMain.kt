package cn.sincky.plugin


import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.ContactCommandSender
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages


object AntiRecallPluginMain : PluginBase() {

    private var antiRecall : AntiRecall? = null

    override fun onLoad() {
        super.onLoad()
        antiRecall = AntiRecall()
    }

    override fun onEnable() {
        super.onEnable()
        logger.info("AntiRecall Plugin loaded!")
        registerCommands()

        subscribeGroupMessages {
            always {
                launch {
                    //reply("${message[MessageSource].id}")
                    antiRecall!!.saveMessage(group.id,message)
                }
            }
        }


        subscribeAlways<MessageRecallEvent> { event ->
            launch {
                if (event is MessageRecallEvent.GroupRecall){
                    AntiRecallPluginMain.logger.info("��⵽�����Ϣ${authorId} ${event.messageId}")
                    antiRecall!!.antiRecallByGroupEvent(event)
                }
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
        AntiRecallPluginMain.logger.info("onDisable")
        antiRecall = null
    }

    // ע������
    private fun registerCommands() {
        registerCommand {
            name = "AntiRecall"
            alias = listOf()
            description = "AntiRecall����������"
            usage = "[/AntiRecall enable] �򿪱�Ⱥ�ķ����ع���(����Ⱥ�����)\n" +
                    "[/AntiRecall disable] �رձ�Ⱥ�ķ����ع���(����Ⱥ�����)\n" +
                    "[/AntiRecall enable Ⱥ��] ��ָ��Ⱥ�ķ����ع���\n" +
                    "[/AntiRecall disable Ⱥ��] �ر�ָ��Ⱥ�ķ����ع���"
            onCommand {
                if (it.isEmpty()) {
                    return@onCommand false
                }
                when (it[0].toLowerCase()) {
                    "enable" -> {
                        val groupID: Long  = if (it.size == 1) {
                            if(this is ContactCommandSender && this.contact is Group){ //�ж��Ƿ���Ⱥ�﷢�͵�����
                                this.contact.id
                            }else{
                                return@onCommand false
                            }
                        } else {
                            it[1].toLong()
                        }
                        antiRecall!!.setAntiRecallStatus(groupID,true)
                        this.sendMessage("Ⱥ${groupID}:�Ѵ򿪷����ع���")
                        return@onCommand true
                    }
                    "disable" -> {
                        val groupID = if (it.size == 1) {
                            if(this is ContactCommandSender && this.contact is Group){ //�ж��Ƿ���Ⱥ�﷢�͵�����
                                this.contact.id
                            }else{
                                return@onCommand false
                            }
                        } else {
                            it[1].toLong()
                        }
                        antiRecall!!.setAntiRecallStatus(groupID,false)
                        this.sendMessage("Ⱥ${groupID}:�ѹرշ����ع���")
                        return@onCommand true
                    }
                    else -> {
                        return@onCommand false
                    }
                }
            }
        }
    }


}