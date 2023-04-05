package ru.yarsu

import io.pebbletemplates.pebble.PebbleEngine
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.writer

object Main {
    private const val OUTPUT_DIRECTORY = "site"

    @JvmStatic
    fun main(args: Array<String>) {
        val scheduleInfo = formSchedule()
        val pebbleEngine = configureEngine()
        // Создаём целевые каталоги
        Files.createDirectories(Paths.get(OUTPUT_DIRECTORY, "courses"))

        // Помещаем данные в шаблон "some.peb" и выводим на экран
        generateDataFromSomePeb(scheduleInfo, pebbleEngine)

        //printTemplateToFile(pebbleEngine, "course-list.peb", "course-list.html", emptyMap())
        //printTemplateToFile(pebbleEngine, "course.peb", "courses/course.html", emptyMap())
       // printTemplateToFile(pebbleEngine, "schedule.peb", "schedule.html", emptyMap())
    }

    private fun generateDataFromSomePeb(
        scheduleInfo: ScheduleInfo,
        pebbleEngine: PebbleEngine
    ) {
        // Формируем контекст, который необходим шаблону для отображения данных
        val context = mutableMapOf<String, Any>()
        //Создаем файлы для курсов
        for (i in scheduleInfo.courses) {
            context["course"] = i
            printTemplateToFile(pebbleEngine, "course.peb", "courses/${i.id}.html", context)
        }
        //Создаем лист с курсами
        context["coursesList"] = scheduleInfo.courses
        printTemplateToFile(pebbleEngine, "course-list.peb", "course-list.html", context)
        //Создаем лист с распианием
        context["scheduleList"] = scheduleInfo.scheduleItems
        printTemplateToFile(pebbleEngine, "schedule.peb", "schedule.html", context)
        // Первый элемент из списка расписаний
        val itemInfo = scheduleInfo.scheduleItems.first()
        context["scheduleItem"] = itemInfo
        // Находим соответствующий ему курс по идентификатору
        val course = scheduleInfo.courses
            .first { it.id == itemInfo.courseId }
        context["course"] = course

        // Выводим результат в файл some.html
        printTemplateToFile(pebbleEngine, "some.peb", "some.html", context)
    }
    private fun configureEngine(): PebbleEngine {
        return PebbleEngine.Builder().build()
    }

    private fun printTemplateToFile(
        pebbleEngine: PebbleEngine,
        templateName: String,
        fileName: String,
        context: Map<String, Any>,
    ) {
        val template = pebbleEngine.getTemplate("ru/yarsu/${templateName}")
        val fileWriter = Paths.get(OUTPUT_DIRECTORY, fileName).writer()
        try {
            template.evaluate(fileWriter, context)
        } catch (exception: IOException) {
            System.err.apply {
                println("Не могу обработать шаблон $templateName")
                println(exception.message)
            }
        }
    }
}