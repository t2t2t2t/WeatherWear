document.addEventListener('DOMContentLoaded', function () {
    // === 1. Логика переключения темы ===
    const toggleSwitch = document.querySelector('#checkbox');
    const themeLabel = document.querySelector('.theme-label');
    const currentTheme = localStorage.getItem('theme');

    function updateThemeLabel() {
        themeLabel.textContent = document.body.className === 'dark-theme' ? '☽' : '☀';
    }

    if (currentTheme) {
        document.body.className = currentTheme;
        toggleSwitch.checked = currentTheme === 'dark-theme';
    }
    updateThemeLabel();

    toggleSwitch.addEventListener('change', function (e) {
        if (e.target.checked) {
            document.body.className = 'dark-theme';
            localStorage.setItem('theme', 'dark-theme');
        } else {
            document.body.className = 'light-theme';
            localStorage.setItem('theme', 'light-theme');
        }
        updateThemeLabel();
    });


    // === 2. Логика геолокации ===
    const getLocationBtn = document.getElementById('getLocationBtn');

    if (!getLocationBtn) {
        console.warn('Кнопка #getLocationBtn не найдена');
        return;
    }

    getLocationBtn.addEventListener('click', function () {
        if (!navigator.geolocation) {
            alert("Геолокация не поддерживается вашим браузером.");
            return;
        }

        // Меняем текст кнопки
        this.disabled = true;
        this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Определение...';

        navigator.geolocation.getCurrentPosition(
            function (position) {
                const lat = position.coords.latitude;
                const lon = position.coords.longitude;

                fetch('/weather-by-coords', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({lat: lat, lon: lon})
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Сервер вернул статус: ' + response.status);
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.error) {
                            alert("Ошибка: " + data.error);
                        } else {
                            // Находим поле ввода и форму
                            const cityInput = document.querySelector('input[name="city"]');
                            const form = document.querySelector('form');

                            if (cityInput && form) {
                                cityInput.value = data.city;
                                form.submit(); // Отправляем форму
                            } else {
                                alert("Не удалось найти форму или поле ввода");
                            }
                        }
                    })
                    .catch(err => {
                        console.error("Ошибка:", err);
                        alert("Ошибка: " + (err.message || "Неизвестная ошибка"));
                    })
                    .finally(() => {
                        const btn = document.getElementById('getLocationBtn');
                        if (btn) {
                            btn.disabled = false;
                            btn.innerHTML = '<i class="fas fa-location-arrow"></i> Мое местоположение';
                        }
                    });
            },
            function (error) {
                let message = '';
                switch (error.code) {
                    case error.PERMISSION_DENIED:
                        message = "Пользователь отклонил запрос на определение местоположения.";
                        break;
                    case error.POSITION_UNAVAILABLE:
                        message = "Информация о местоположении недоступна.";
                        break;
                    case error.TIMEOUT:
                        message = "Истекло время ожидания.";
                        break;
                    default:
                        message = "Неизвестная ошибка при определении местоположения.";
                }
                alert(message);
                const btn = document.getElementById('getLocationBtn');
                if (btn) {
                    btn.disabled = false;
                    btn.innerHTML = '<i class="fas fa-location-arrow"></i> Мое местоположение';
                }
            }
        );
    });
});
