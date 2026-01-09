/**
 * admin-user-create.js
 * 学生一括登録画面の用スクリプト
 */
document.addEventListener('DOMContentLoaded', function () {
    const textarea = document.getElementById('studentListRaw');
    const lineNumbers = document.getElementById('line-numbers');

    const updateLineNumbers = () => {
        if (!textarea || !lineNumbers) return;

        console.log('Updating line numbers...');
        const numberOfLines = textarea.value.split('\n').length;
        const currentDisplayLines = lineNumbers.innerText.split('\n').filter(l => l.trim() !== '').length;
        
        // 常に更新（デバッグのため）
        const targetLines = Math.max(numberOfLines, 10);
        
        // 数が違う、または現在空の場合に更新
        if (targetLines !== currentDisplayLines || lineNumbers.innerHTML.trim() === '1') {
            const lines = Array.from({length: targetLines}, (_, i) => i + 1);
            lineNumbers.innerHTML = lines.join('<br>');
        }
    };

    const syncScroll = () => {
        if (!textarea || !lineNumbers) return;
        lineNumbers.scrollTop = textarea.scrollTop;
    };

    if (textarea && lineNumbers) {
        textarea.addEventListener('input', updateLineNumbers);
        textarea.addEventListener('scroll', syncScroll);
        updateLineNumbers();
        setTimeout(updateLineNumbers, 100);
    } else {
        console.error('Textarea or line-numbers element not found');
    }
});
