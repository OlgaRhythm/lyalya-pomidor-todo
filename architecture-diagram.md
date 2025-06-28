# Архитектурная диаграмма Lyalya Pomidor Todo

Эта диаграмма показывает архитектуру приложения после миграции на Jetpack Compose, включая все слои и их взаимодействие.

```mermaid
graph TB
    subgraph "UI Layer (Compose)"
        A["PomodoroScreen"] --> B["CategoryCard"]
        A --> C["TimerComponent"]
        B --> D["EditCategoryDialog"]
        B --> E["DeleteCategoryDialog"]
    end
    
    subgraph "ViewModel Layer"
        F["PomodoroViewModel"] --> G["TimerState"]
        F --> H["Category LiveData"]
    end
    
    subgraph "Data Layer"
        I["AppDatabase (Room)"] --> J["CategoryDao"]
        I --> K["SessionDao"]
        I --> L["TaskDao"]
        
        M["Category Entity"]
        N["Session Entity"] 
        O["Task Entity"]
    end
    
    subgraph "Background Services"
        P["TimerWorker"]
    end
    
    A --> F
    F --> I
    F --> P
    
    J --> M
    K --> N
    L --> O
    
    G -.-> |"State Updates"| C
    H -.-> |"LiveData"| B
    
    style A fill:#e1f5fe
    style F fill:#f3e5f5  
    style I fill:#e8f5e8
    style P fill:#fff3e0
```

## Описание компонентов

### UI Layer (Compose)
- **PomodoroScreen**: Главный экран приложения
- **CategoryCard**: Карточки категорий с управлением
- **TimerComponent**: Круговой таймер с кнопками управления
- **EditCategoryDialog**: Диалог редактирования категории
- **DeleteCategoryDialog**: Диалог подтверждения удаления

### ViewModel Layer
- **PomodoroViewModel**: Основная логика приложения
- **TimerState**: Состояние таймера (время, пауза, прогресс)
- **Category LiveData**: Реактивные данные категорий

### Data Layer
- **AppDatabase**: Room база данных
- **CategoryDao**: Операции с категориями
- **SessionDao**: Операции с сессиями Pomodoro
- **TaskDao**: Операции с задачами
- **Entity классы**: Модели данных для БД

### Background Services
- **TimerWorker**: Фоновая работа таймера 