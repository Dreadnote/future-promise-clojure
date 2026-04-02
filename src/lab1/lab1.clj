(ns lab1.lab1
  (:gen-class))

;; Функция, выполняющая основное вычисление:
;; возводит каждое число в квадрат и суммирует результаты
(defn complex-compute [numbers]
  (println "  Поток начал вычисление...")
  (let [result (->> numbers
                    (map #(* % %))      ; возведение в квадрат
                    (reduce +))]        ; суммирование
    (println "  Поток закончил, результат:" result)
    result))

;; Последовательное вычисление:
;; делит список пополам и вычисляет сумму квадратов для каждой половины
(defn sequential-compute [numbers]
  (println "Последовательное выполнение (без future):")
  (let [start (System/currentTimeMillis)
        half-size (/ (count numbers) 2)
        left (take half-size numbers)   ; левая половина
        right (drop half-size numbers)] ; правая половина
    (def result1 (complex-compute left))
    (def result2 (complex-compute right))
    (let [end (System/currentTimeMillis)]
      (println "---")
      (println "Общий результат:" (+ result1 result2))
      (println "Время выполнения:" (- end start) "мс")
      (- end start))))

;; Параллельное вычисление с 2 потоками:
;; использует future и promise для одновременного вычисления обеих половин
(defn parallel-compute-2 [numbers]
  (println "Параллельное выполнение (2 потока с future и promise):")
  (let [start (System/currentTimeMillis)
        half-size (/ (count numbers) 2)
        left (take half-size numbers)
        right (drop half-size numbers)
        p1 (promise)   ; обещание для результата левой половины
        p2 (promise)]  ; обещание для результата правой половины
    (future (deliver p1 (complex-compute left)))   ; поток 1
    (future (deliver p2 (complex-compute right)))  ; поток 2
    (let [result1 @p1
          result2 @p2
          end (System/currentTimeMillis)]
      (println "---")
      (println "Общий результат:" (+ result1 result2))
      (println "Время выполнения:" (- end start) "мс")
      (- end start))))

;; Параллельное вычисление с 4 потоками:
;; делит список на 4 части и вычисляет каждую в отдельном потоке
(defn parallel-compute-4 [numbers]
  (println "Параллельное выполнение (4 потока с future и promise):")
  (let [start (System/currentTimeMillis)
        quarter-size (/ (count numbers) 4)
        part1 (take quarter-size numbers)                           ; часть 1
        part2 (take quarter-size (drop quarter-size numbers))       ; часть 2
        part3 (take quarter-size (drop (* 2 quarter-size) numbers)) ; часть 3
        part4 (drop (* 3 quarter-size) numbers)                     ; часть 4
        p1 (promise)
        p2 (promise)
        p3 (promise)
        p4 (promise)]
    (future (deliver p1 (complex-compute part1)))   ; поток 1
    (future (deliver p2 (complex-compute part2)))   ; поток 2
    (future (deliver p3 (complex-compute part3)))   ; поток 3
    (future (deliver p4 (complex-compute part4)))   ; поток 4
    (let [result1 @p1
          result2 @p2
          result3 @p3
          result4 @p4
          end (System/currentTimeMillis)]
      (println "---")
      (println "Общий результат:" (+ result1 result2 result3 result4))
      (println "Время выполнения:" (- end start) "мс")
      (- end start))))

;; Главная функция: запускает все три варианта и выводит сравнение
(defn -main [& args]
  (let [size 2000000                           ; 2 миллиона чисел
        numbers (range 1 (inc size))]          ; список от 1 до 2 000 000

    (println "ЛАБОРАТОРНАЯ РАБОТА: future и promise")
    (println "========================================")
    (println (str "Размер данных: " size " чисел"))
    (println (str "Операция: возведение в квадрат каждого числа и суммирование"))
    (println (str "Процессоров: " (.availableProcessors (Runtime/getRuntime))))
    (println "========================================")
    (println "")

    ;; Последовательное выполнение
    (def seq-time (sequential-compute numbers))
    (println "")

    ;; Параллельное выполнение с 2 потоками
    (def par2-time (parallel-compute-2 numbers))
    (println "")

    ;; Параллельное выполнение с 4 потоками
    (def par4-time (parallel-compute-4 numbers))
    (println "")

    ;; Вывод сравнения
    (println "========================================")
    (println "ИТОГОВОЕ СРАВНЕНИЕ:")
    (println (str "  Последовательно (1 поток):    " seq-time " мс"))
    (println (str "  Параллельно (2 потока):       " par2-time " мс"))
    (println (str "  Параллельно (4 потока):       " par4-time " мс"))
    (println "")

    (def gain-2 (- seq-time par2-time))
    (def gain-4 (- seq-time par4-time))

    (println (str "  Выигрыш 2 потоков: " gain-2 " мс ("
                  (format "%.1f" (float (/ gain-2 seq-time 1/100))) "%)"))
    (println (str "  Выигрыш 4 потоков: " gain-4 " мс ("
                  (format "%.1f" (float (/ gain-4 seq-time 1/100))) "%)"))

    ;; Завершаем агенты (освобождаем ресурсы)
    (shutdown-agents)))