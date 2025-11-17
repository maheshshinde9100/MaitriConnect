const StoryStrip = ({ stories = [] }) => {
  if (!stories.length) return null

  return (
    <div className="flex gap-4 overflow-x-auto pb-4 custom-scrollbar">
      {stories.map((story) => (
        <div key={story.id} className="flex flex-col items-center text-center">
          <div
            className={[
              'h-16 w-16 rounded-full border-4 p-1',
              story.isNew ? 'border-sky-400' : 'border-slate-200',
            ].join(' ')}
          >
            <img
              src={story.avatar}
              alt={story.name}
              className="h-full w-full rounded-full object-cover"
            />
          </div>
          <p className="mt-2 w-20 truncate text-xs text-slate-500">{story.name}</p>
        </div>
      ))}
    </div>
  )
}

export default StoryStrip

