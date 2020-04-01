package cn.sincky.plugin

import net.mamoe.mirai.console.command.ContactCommandSender
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.*


object DrawLotsPluginMain : PluginBase() {

    private var drawLots: DrawLots? = null
    private val groupList = mutableSetOf<Long>()
    private const val lostPath = "lots.yml"

    override fun onLoad() {
        super.onLoad()
        drawLots = DrawLots(lostPath)
        logger.info("DrawLots init success")
    }

    override fun onEnable() {
        super.onEnable()
        registerCommands()

        subscribeGroupMessages {
            (contains("��ǩ")){
                //logger.info(senderName + "��ǩ")
                if (groupList.contains(this.group.id)){
                    this.reply(sender.at() + "\n" +
                            drawLots!!.sign(sender.id))
                }
            }
            (contains("��ǩ")){
                //logger.info(senderName + "��ǩ")
                if (groupList.contains(this.group.id)) {
                    this.reply(
                        sender.at() + "\n" +
                                drawLots!!.unSign(sender.id)
                    )
                }
            }

        }

    }

    override fun onDisable() {
        super.onDisable()
        drawLots = null
    }

    // ע������
    private fun registerCommands() {
        registerCommand {
            name = "DrawLots"
            alias = listOf("DL")
            description = "DrawLots����������"
            usage = "[/DL enable] �򿪱�Ⱥ�ĳ�ǩ����(����Ⱥ�����)\n" +
                    "[/DL disable] �رձ�Ⱥ�ĳ�ǩ����(����Ⱥ�����)\n" +
                    "[/DL enable Ⱥ��] ��ָ��Ⱥ�ĳ�ǩ����\n" +
                    "[/DL disable Ⱥ��] �ر�ָ��Ⱥ�ĳ�ǩ����"
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
                        groupList.add(groupID)
                        this.sendMessage("Ⱥ${groupID}:�Ѵ򿪳�ǩ����")
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
                        groupList.remove(groupID)
                        this.sendMessage("Ⱥ${groupID}:�ѹرճ�ǩ����")
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