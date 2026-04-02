(ns lab1.lab1
  (:gen-class))

(defn sum-half1 [numbers]
  (println "  Левая половина: начали вычисление...")
  (let [half (take (/ (count numbers) 2) numbers)
        result (apply + half)]
    (println "  Левая половина: готово =" result)
    result))

(defn sum-half2 [numbers]
  (println "  Правая половина: начали вычисление...")
  (let [half (drop (/ (count numbers) 2) numbers)
        result (apply + half)]
    (println "  Правая половина: готово =" result)
    result))

(defn sequential-compute [numbers]
  (println "Последовательное выполнение (без future):")
  (let [start (System/currentTimeMillis)
        left (sum-half1 numbers)
        right (sum-half2 numbers)
        end (System/currentTimeMillis)]
    (println "---")
    (println "Результаты:")
    (println "  Сумма левой половины =" left)
    (println "  Сумма правой половины =" right)
    (println "  Общая сумма =" (+ left right))
    (println "Время выполнения:" (- end start) "мс")
    (- end start)))

(defn parallel-compute [numbers]
  (println "Параллельное выполнение (с future и promise):")
  (let [start (System/currentTimeMillis)
        left-promise (promise)
        right-promise (promise)]
    (future
      (let [result (sum-half1 numbers)]
        (deliver left-promise result)
        result))
    (future
      (let [result (sum-half2 numbers)]
        (deliver right-promise result)
        result))
    (let [left @left-promise
          right @right-promise
          end (System/currentTimeMillis)]
      (println "---")
      (println "Результаты:")
      (println "  Сумма левой половины =" left)
      (println "  Сумма правой половины =" right)
      (println "  Общая сумма =" (+ left right))
      (println "Время выполнения:" (- end start) "мс")
      (- end start))))

(defn -main [& args]
  (let [numbers (range 1 2000000)]  ; 2 миллиона чисел
    (println "Размер списка:" (count numbers) "чисел")
    (println "Первые 10 чисел:" (take 10 numbers) "...")
    (println "Последние 10 чисел:" (take-last 10 numbers) "...")
    (println "========================")

    (def sequential-time (sequential-compute numbers))
    (println "========================")

    (def parallel-time (parallel-compute numbers))
    (println "========================")

    (println "СРАВНЕНИЕ:")
    (println "  Последовательное время:" sequential-time "мс")
    (println "  Параллельное время:" parallel-time "мс")

    (if (< parallel-time sequential-time)
      (println "  Параллельное вычисление быстрее на:" (- sequential-time parallel-time) "мс")
      (println "  Последовательное вычисление быстрее на:" (- parallel-time sequential-time) "мс"))

    (shutdown-agents)))