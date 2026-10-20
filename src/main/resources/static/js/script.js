function scrollCategory(direction) {
    const slider = document.getElementById('categoryScrollWrapper');
    const scrollAmount = 400; // Khoảng cách trượt mỗi lần click
    if (direction === 'left') {
        slider.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
        slider.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
}