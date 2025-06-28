# Диаграмма состояний Pomodoro таймера

Эта диаграмма показывает все возможные состояния таймера Pomodoro и переходы между ними.

```mermaid
stateDiagram-v2
    [*] --> Idle: "App Launch"
    
    Idle --> WorkSession: "Start Timer<br/>25 min session"
    
    WorkSession --> Paused: "Pause"
    Paused --> WorkSession: "Resume"
    
    WorkSession --> BreakTime: "Work Completed<br/>Save session to DB"
    Paused --> Idle: "Stop<br/>Save partial session"
    
    BreakTime --> Idle: "Break Completed<br/>Ready for next session"
    BreakTime --> WorkSession: "Skip Break<br/>Start new work session"
    
    WorkSession --> Idle: "Manual Stop<br/>Save session data"
    
    note right of WorkSession
        - Timer counting down
        - Session time tracking
        - Real-time UI updates
    end note
    
    note right of BreakTime
        - 5 minute break
        - Different UI color
        - Optional skip function
    end note
    
    note left of Idle
        - Display categories
        - Show total time spent
        - Category management
    end note
```

## Описание состояний

### Idle (Покой)
- Начальное состояние приложения
- Отображение списка категорий
- Показ общего накопленного времени
- Управление категориями (создание, редактирование, удаление)

### WorkSession (Рабочая сессия)
- Активный 25-минутный таймер
- Обратный отсчет времени
- Отслеживание времени текущей сессии
- Возможность поставить на паузу или остановить

### Paused (Пауза)
- Таймер приостановлен
- Время не тикает
- Возможность возобновить или остановить
- Сохранение промежуточного состояния

### BreakTime (Перерыв)
- 5-минутный перерыв после завершения работы
- Другой цвет интерфейса
- Автоматический переход в Idle после завершения
- Возможность пропустить перерыв

## Переходы между состояниями

1. **Start Timer**: Idle → WorkSession
2. **Pause**: WorkSession → Paused  
3. **Resume**: Paused → WorkSession
4. **Work Complete**: WorkSession → BreakTime
5. **Stop (from Work)**: WorkSession → Idle
6. **Stop (from Pause)**: Paused → Idle
7. **Break Complete**: BreakTime → Idle
8. **Skip Break**: BreakTime → WordSession

## Сохранение данных

- **При завершении работы**: Сессия сохраняется в базу данных
- **При остановке**: Частичная сессия сохраняется
- **Накопление времени**: Общее время категории обновляется 