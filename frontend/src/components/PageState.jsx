const PageState = ({ title, description, action, icon: Icon }) => {
  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-3 p-8 text-center text-slate-500">
      {Icon && <Icon size={42} className="text-slate-400" />}
      <div>
        <p className="text-lg font-semibold text-slate-700">{title}</p>
        {description && <p className="mt-1 text-sm text-slate-500">{description}</p>}
      </div>
      {action}
    </div>
  )
}

export default PageState

